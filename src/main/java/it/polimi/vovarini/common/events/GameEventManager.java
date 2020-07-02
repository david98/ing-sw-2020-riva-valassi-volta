package it.polimi.vovarini.common.events;

import org.codehaus.plexus.util.ExceptionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles event raising and listening through the use of reflection.
 *
 * @author Davide Volta
 * @version 0.1
 */
public class GameEventManager {

  private static final Logger LOGGER = Logger.getLogger(GameEventManager.class.getName());

  private static GameEventManager instance;

  private final HashMap<String, Set<Map.Entry<Object, Method>>> listeners;

  public static synchronized GameEventManager getInstance() {
    if (instance == null) {
      instance = new GameEventManager();
    }
    return instance;
  }

  public GameEventManager() {
    listeners = new HashMap<>();
  }

  /**
   * Checks for methods which are annotated with {@link GameEventListener}
   * and binds them to the specified {@link GameEvent}.
   *
   * @param obj The object containing the listeners.
   */
  @SuppressWarnings("unchecked")
  public static synchronized void bindListeners(@org.jetbrains.annotations.NotNull Object obj) {
    for (Method m : obj.getClass().getMethods()) {
      GameEventListener a = m.getDeclaredAnnotation(GameEventListener.class);
      if (a != null) {
        Class<?>[] parameterTypes = m.getParameterTypes();
        try {
          Class<? extends GameEvent> eventClass = (Class<? extends GameEvent>) parameterTypes[0];
          if (GameEvent.class.isAssignableFrom(eventClass)) {
            if (parameterTypes.length == 1) {
              getInstance().register(obj, m, eventClass);
            } else {
              LOGGER.log(Level.SEVERE, "{0}: a listener can only have 1 parameter!", m.getName());
            }
          } else {
            LOGGER.log(Level.SEVERE, "Expected listener parameter to inherit from GameEvent but {0} was found instead.",
                    eventClass);
          }
        } catch (ClassCastException e) {
          LOGGER.log(Level.SEVERE, "{0} {1}", new Object[]{e.toString(), e});
        }
      }
    }
  }

  // maybe add bind method for static methods?

  private synchronized void register(Object obj, Method method, Class<? extends GameEvent> eventClass) {
    Set<Map.Entry<Object, Method>> eventClassListeners = listeners.computeIfAbsent(eventClass.getSimpleName(),
            k -> new HashSet<>());
    eventClassListeners.add(new AbstractMap.SimpleEntry<>(obj, method));
  }

  /**
   * Calls all registered listeners for the given event.
   *
   * @param e The event to be raised.
   */
  public static synchronized void raise(GameEvent e) {
    LOGGER.setLevel(Level.FINE);
    LOGGER.log(Level.FINE, "GameEvent of class {0} was raised.", new Object[]{e.getClass().getName()});
    Set<Map.Entry<Object, Method>> eventListeners = getInstance().listeners.get(e.getClass().getSimpleName());
    if (eventListeners != null) {
      for (Map.Entry<Object, Method> pair : eventListeners) {
        try {
          pair.getValue().invoke(pair.getKey(), e.getClass().cast(e));
        } catch (IllegalAccessException ex) {
          LOGGER.log(Level.SEVERE, "{0}", new Object[]{ex});
        } catch (InvocationTargetException ex) {
          LOGGER.log(Level.SEVERE, "Exception occurred while calling listener {0}. " +
                          "Stack trace: {1}",
                  new Object[]{
                          pair.getValue(),
                          ExceptionUtils.getFullStackTrace(ex.getTargetException())
                  });
        }
      }
    }
  }
}
