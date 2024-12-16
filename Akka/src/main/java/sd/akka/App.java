package sd.akka;

import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.lang.Runtime;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.routing.RoundRobinPool;
import sd.akka.actor.*;


public class App {
	public static void main(String[] args) {
	
        final ActorSystem system = ActorSystem.create("ActorSystem");

        // Création de l'anneau avec 8 noeuds
        ActorRef anneau = system.actorOf(Anneau.props(8), "anneau");

        // Déclenchement de l'élection
        anneau.tell(new Anneau.AnneauInterface.Election(), ActorRef.noSender());

        // Ajout d'un hook pour l'arrêt propre du système
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            system.terminate();
            try {
                system.getWhenTerminated().toCompletableFuture().get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
	}
}
