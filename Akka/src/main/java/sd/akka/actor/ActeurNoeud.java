package sd.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ActorRef;
import akka.pattern.Patterns;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CompletionStage;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class ActeurNoeud extends AbstractActor {
    private ActorRef voisinDroite;
    private boolean passif = false;
    private boolean leader = false;
    private final int id;

    private ActeurNoeud(int id) {
        this.id = id;
    }

    public static Props props(int id) {
        return Props.create(ActeurNoeud.class, () -> new ActeurNoeud(id));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(Message.Tour.class, this::handleTour)
                .match(Message.SetVoisinDroite.class, this::handleSetVoisinDroite)
                .match(Message.GetLeaderStatus.class, this::handleGetLeaderStatus)
                .build();
    }

	public int getId(){
		return id;
	}
    private void handleTour(Message.Tour message) {
        if (passif) {
            voisinDroite.tell(message, getSelf());
        } else {
            if (message.id > id) {
                passif = true;
                voisinDroite.tell(message, getSelf());
            } else if (message.id < id) {
                // Ignore the message
            } else {
                leader = true;
                System.out.println("Le nœud " + id + " est élu leader !");
            }
        }
    }

    private void handleSetVoisinDroite(Message.SetVoisinDroite message) {
        this.voisinDroite = message.voisin;
    }

    private void handleGetLeaderStatus(Message.GetLeaderStatus message) {
        getSender().tell(leader, getSelf());
    }

    public interface Message {
        class Tour implements Message {
            public final int id;

            public Tour(int id) {
                this.id = id;
            }
			
        }

        class SetVoisinDroite implements Message {
            public final ActorRef voisin;

            public SetVoisinDroite(ActorRef voisin) {
                this.voisin = voisin;
            }
        }

        class GetLeaderStatus implements Message {}
    }
}
