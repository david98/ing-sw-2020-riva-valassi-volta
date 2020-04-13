package it.polimi.vovarini.common.events;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GameEventListener {
  Class<? extends GameEvent> eventClass();
}