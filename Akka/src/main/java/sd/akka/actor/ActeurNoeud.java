package sd.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ActorRef;
import akka.pattern.Patterns;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CompletionStage;

public class ActeurNoeud extends AbstractActor{
	private ActorRef voisinDroite;
	private boolean passif = false;
	private boolean leader = false;
	private final int id;

	private ActeurNoeud(int id){
		this.id = id;
	}

	public void tour(Tour i){
		if(this.passif){
			this.voisinDroite = i.getId();
			voisinDroite.tell(i, getSelf());
		}
		else{
			if(i>this.id){
				System.out.println("Noeud "+ this.id+" passif: "+ i);
				this.passif = true;
				this.voisinDroite.tell(i, getSelf());
			}
			if(i<this.id) {
				System.out.println("Destruction du message: "+ this.id+" > "+ i); 
			}
			if(i = this.id){
				System.out.println("Eléction du leader: "+ this.id); 
				this.leader = true;
			}
		}
	}

	public int getId(){
		return id;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(Tour.class, message -> tour(message))
				.build();
	}

	public static Props props(int n){
		props.create(ActeurNoeud.class, n);
	}

	public interface Message{
		
		public static class Tour implements Message{
			public int id;
			public Tour(int id){
				this.id = id;
			}
		
		}
   }
}

