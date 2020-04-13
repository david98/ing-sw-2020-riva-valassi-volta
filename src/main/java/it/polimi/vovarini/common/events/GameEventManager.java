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
  public static void bindListeners(@org.jetbrains.annotations.NotNull Object obj){
    for (Method m: obj.getClass().getMethods()){
      GameEventListener a = m.getDeclaredAnnotation(GameEventListener.class);
      if (a != null){
        Class<?>[] parameterTypes = m.getParameterTypes();
        Class<? extends GameEvent> eventClass = a.eventClass();
        if (parameterTypes.length == 1 && parameterTypes[0].equals(eventClass)) {
          GameEventManager.getInstance().register(obj, m, eventClass);
        } else {
          throw new Error("Invalid listener"); // TODO: improve
        }
      }
    }
  }

  private void register(Object obj, Method method, Class<? extends GameEvent> eventClass){
    Set<Map.Entry<Object, Method>> eventClassListeners = listeners.computeIfAbsent(eventClass.getSimpleName(),
            k -> new HashSet<>());
    eventClassListeners.add(new AbstractMap.SimpleEntry<>(obj, method));
  }

  public void raise(GameEvent e) {
    Set<Map.Entry<Object, Method>> eventListeners = listeners.get(e.getClass().getSimpleName());
    if (eventListeners != null){
      for (Map.Entry<Object, Method> pair: eventListeners){
        try {
          pair.getValue().invoke(pair.getKey(), e.getClass().cast(e));
        } catch (InvocationTargetException | IllegalAccessException ex){
          ex.printStackTrace();
        }
      }
    }
  }
}
