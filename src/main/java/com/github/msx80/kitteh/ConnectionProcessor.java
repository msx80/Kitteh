package com.github.msx80.kitteh;

/**
 * This interface enables the user to delay execution of the response to a connection.
 * You can use this to defer connection processing to a later time or to a different thread.
 * You can store the passed connection in a list or queue to be processed from another actor.
 * @author msx
 *
 */
public interface ConnectionProcessor
{
    void processConnection(Runnable connection);
}
