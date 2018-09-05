/**
 * Copyright (c) 2018 SUSE LLC
 *
 * This software is licensed to you under the GNU General Public License,
 * version 2 (GPLv2). There is NO WARRANTY for this software, express or
 * implied, including the implied warranties of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
 * along with this software; if not, see
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
 *
 * Red Hat trademarks are not licensed under GPLv2. No permission is
 * granted to use or replicate Red Hat trademarks that are incorporated
 * in this software or its documentation.
 */

package com.suse.salt.netapi.event;

import com.suse.salt.netapi.datatypes.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implements basic listener bookkeeping for EventStream.
 */
public abstract class AbstractEventStream implements EventStream {

    /**
     * Listeners that are notified of a new events.
     */
    private final List<EventListener> listeners = new CopyOnWriteArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEventListener(EventListener listener) {
        listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEventListener(EventListener listener) {
        listeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getListenerCount() {
        return listeners.size();
    }

    /**
     * Notifies all listeners of an event
     *
     * @param event the event
     */
    protected void notifyListeners(Event event) {
        for (EventListener listener : new ArrayList<>(listeners)) {
            listener.notify(event);
        }
    }

    /**
     * Removes all listeners.
     *
     * @param code an integer code to represent the reason for closing
     * @param phrase a String representation of code
     */
    protected void clearListeners(int code, String phrase) {
        listeners.forEach(listener -> listener.eventStreamClosed(code, phrase));

        // Clear out the listeners
        listeners.clear();
    }
}
