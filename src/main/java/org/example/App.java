package org.example;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import org.example.actor.BusActor;
import org.example.actor.StationActor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class App {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("BUS-STATION-PROBLEM");
        List<ActorRef> stations = IntStream.range(1, 4)
                .boxed()
                .map(i -> system.actorOf(Props.create(StationActor.class, "N-" + i)))
                .collect(Collectors.toList());

        List<ActorRef> busses = IntStream.range(1, 11)
                .boxed()
                .map(i -> system.actorOf(Props.create(BusActor.class, stations, "Bus-" + i)))
                .collect(Collectors.toList());

        try {
            Thread.sleep(30000);
            busses.forEach(system::stop);
            stations.forEach(system::stop);
            system.terminate();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
