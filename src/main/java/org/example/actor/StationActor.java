package org.example.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StationActor extends AbstractActor {

    private static final Integer MAX = 2;
    private final List<ActorRef> stayed = new ArrayList<>();
    private Integer allocated = 0;
    private String name;

    public StationActor(String name) {
        this.name = name;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchEquals(BusActor.Command.CAN_I_STOP, command -> checkIfAvailable())
                .matchEquals(BusActor.Command.STOP_ON_STATION, command -> registerNewBus())
                .matchEquals(BusActor.Command.LEAVING, command -> registerLeave())
                .build();
    }

    private void registerLeave() {
        allocated--;
        stayed.remove(sender());
    }

    private void registerNewBus() {
        stayed.add(sender());
    }

    private void checkIfAvailable() {
        if (allocated < MAX) {
            allocated++;
           sender().tell(Command.STATION_IS_EMPTY, self());
           if(Objects.equals(allocated, MAX)) {
               System.out.println(name + " full");
           }
        } else {
            sender().tell(Command.STATION_IS_FULL, self());
        }
    }

    public enum Command {
        STATION_IS_EMPTY,
        STATION_IS_FULL
    }
}
