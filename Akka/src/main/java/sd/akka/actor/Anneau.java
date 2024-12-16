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


public class Anneau extends AbstractActor {
    private final List<ActorRef> noeuds = new ArrayList<>();

    private Anneau(int nb) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < nb; i++) ids.add(i);
        Collections.shuffle(ids);

        for (int id : ids) {
            ActorRef noeud = getContext().actorOf(ActeurNoeud.props(id));
            noeuds.add(noeud);
        }

        for (int i = 0; i < noeuds.size(); i++) {
            ActorRef current = noeuds.get(i);
            ActorRef next = noeuds.get((i + 1) % noeuds.size());
            current.tell(new ActeurNoeud.Message.SetVoisinDroite(next), getSelf());
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(AnneauInterface.Election.class, msg -> startElection())
                .build();
    }

    public void startElection() {
        for (ActorRef noeud : noeuds) {
            noeud.tell(new ActeurNoeud.Message.Tour(noeud.getId()), getSelf());
        }

        noeuds.forEach(noeud -> {
            CompletionStage<Object> future = Patterns.ask(noeud, new ActeurNoeud.Message.GetLeaderStatus(), Duration.ofSeconds(1));
            future.thenAccept(response -> {
                if (response instanceof Boolean && (Boolean) response) {
                    System.out.println("Un leader a été élu !");
                }
            }).exceptionally(ex -> {
                System.err.println("Erreur lors de la vérification du statut du leader : " + ex.getMessage());
                return null;
            });
        });
    }

    public static Props props(int nb) {
        return Props.create(Anneau.class, nb);
    }

    public interface AnneauInterface {
        class Election implements AnneauInterface {}
    }
}
