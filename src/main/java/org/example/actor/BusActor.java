package org.example.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.List;
import java.util.Random;

public class BusActor extends AbstractActor {
    private final List<ActorRef> stations;
    private ActorRef nearest;
    private final String name;

    public BusActor(List<ActorRef> stations, String name) {
        this.stations = stations;
        this.name = name;
        tryStopOnStation();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals(StationActor.Command.STATION_IS_EMPTY, command -> stopOnStation())
                .matchEquals(StationActor.Command.STATION_IS_FULL, command -> tryStopOnStation())
                .build();
    }

    private void tryStopOnStation() {
        nearest = getNearestStation();
        nearest.tell(Command.CAN_I_STOP, self());
    }

    private ActorRef getNearestStation() {
        return stations.get((int) (Math.random() * stations.size()));
    }

    private void stopOnStation() {
        nearest.tell(Command.STOP_ON_STATION, self());
        System.out.println(name + " stopped");
        try {
            Thread.sleep(5000);
            if (Math.random() > 0.7) {
                leave();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void leave() {
        nearest.tell(Command.LEAVING, ActorRef.noSender());
        System.out.println(name + " leaves station");
    }

    public enum Command {
        STOP_ON_STATION,
        CAN_I_STOP,
        LEAVING
    }
}
