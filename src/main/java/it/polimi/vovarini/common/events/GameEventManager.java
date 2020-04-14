package it.polimi.vovarini.common.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class GameEventManager {

  private static GameEventManager instance;

  private HashMap<String, Set<Map.Entry<Object, Method>>> listeners;

  public static GameEventManager getInstance(){
    if (instance == null){
      instance = new GameEventManager();
    }
    return instance;
  }

  public GameEventManager(){
    listeners = new HashMap<>();
  }

  /**
   * Checks for methods which are annotated with {@link GameEventListener}
   * and binds them to the specified {@link GameEvent}.
   *
   * @param obj The object containing the listeners.
   */
  @SuppressWarnings("unchecked")
  public static void bindListeners(@org.jetbrains.annotations.NotNull Object obj){
    for (Method m: obj.getClass().getMethods()){
      GameEventListener a = m.getDeclaredAnnotation(GameEventListener.class);
      if (a != null){
        Class<?>[] parameterTypes = m.getParameterTypes();
        try {
          Class<? extends GameEvent> eventClass = (Class<? extends GameEvent>) parameterTypes[0];
          if (GameEvent.class.isAssignableFrom(eventClass)) {
            if (parameterTypes.length == 1) {
              getInstance().register(obj, m, eventClass);
            } else {
              throw new Error("A listener can only have 1 parameter!"); // TODO: improve
            }
          } else {
            throw new Error("Expected listener parameter to inherit from GameEvent but " +
                    eventClass + " was found instead.");
          }
        } catch (ClassCastException e){
          e.printStackTrace();
        }
      }
    }
  }

  // TODO: add bind method for static methods

  private void register(Object obj, Method method, Class<? extends GameEvent> eventClass){
    Set<Map.Entry<Object, Method>> eventClassListeners = listeners.computeIfAbsent(eventClass.getSimpleName(),
            k -> new HashSet<>());
    eventClassListeners.add(new AbstractMap.SimpleEntry<>(obj, method));
  }

  public static void raise(GameEvent e) {
    Set<Map.Entry<Object, Method>> eventListeners = getInstance().listeners.get(e.getClass().getSimpleName());
    if (eventListeners != null){
      for (Map.Entry<Object, Method> pair: eventListeners){
        try {
          pair.getValue().invoke(pair.getKey(), e.getClass().cast(e));
        } catch (IllegalAccessException ex){
          ex.printStackTrace();
        } catch (InvocationTargetException ex){
          ex.getTargetException().printStackTrace();
        }
      }
    }
  }
}
