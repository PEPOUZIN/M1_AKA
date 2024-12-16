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



public class Anneau extends AbstractActor{
	//private ActorSystem system;
	private final List<ActorRef> noeuds = new ArrayList<>();
	
	private Anneau(int nb){
		//Création aléatoire de nb
		List<Integer> id = new ArrayList<>();
		for(int i=0; i<nb; i++) id.add(i);
		Collections.shuffle(id);

		//Init des nb en ActeurNoeud
		for(int i=0; i<id.size(); i++){
			 ActorRef noeud = getContext().actorOf(ActeurNoeud.props(id.get(i)));
			 this.noeuds.add(noeud);
		}
		//Init voisin de droite de chaque noeud
		for(int i = 0; i<id.size()-1;i++){
			this.noeuds.get(i).voisinDroite = this.noeud.get(i+1);
		}
		//Le dernier rejoint le premier
		this.noeuds.get(nb).voisinDroite = this.noeud.get(0);
		
   	}

	public void election(){
		boolean lead = false;

		while(lead!=true){
			noeuds.forEach(noeud -> noeud.tell(new ActeurNoeud.Tour(noeud.id()), getSelf()));
			for(int i=0; i< noeuds.size(); i++){
				if(noeuds.get(i).leader=true) lead = true;
			}
		}
	}

	public static Props props(int n){
		props.create(ActeurNoeud.class, n);
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(Election.class, message -> election())
				.build();
	}

	public interface AnneauInterface{
		public static class Election implements AnneauInterface{
			public Election(){
			}
		}
	}
}