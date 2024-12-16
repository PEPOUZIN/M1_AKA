package sd.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ActorRef;
import akka.pattern.Patterns;

import java.math.BigInteger;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CompletionStage;

public class Acteur extends AbstractActor {
    private ActorRef init;
    


	private Acteur(long n) {
        if(n>0) this.init = getContext().actorOf(Acteur.props(n-1));
	}
    @Override
	public Receive createReceive() {
		return receiveBuilder()
				.match(GetMessage.class, message -> getMessage(message))
				.build();
	}


    public static Props props(long n) {
		return Props.create(Acteur.class, n);
	}

	public void getMessage(GetMessage message){
        try{
            int lenght = message.mess.length();
            int nb = new Random().nextInt((lenght-1));
            String al ="abcdefghijklmnopqrstuvwxyz";
            int lt = new Random().nextInt((al.length()-1));
            message.mess = message.mess.replace(message.mess.charAt(nb), al.charAt(lt));
            if(this.init!=null){ 
                init.forward(message,getContext());
                System.out.println("message: "+message.mess);}
                
            else{               
                getSender().tell(message, getSelf());
                //System.out.println("message: "+message.mess); 
            } 
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
	

	

	// Définition des messages en inner classes
	public interface Message {}

    public static class GetMessage implements Message{
        public String mess;
        public GetMessage(String message){
            this.mess=message;
        };


    }

}
/* public class ActeurNoeud extends AbstarctActor{
	private ActorRef voisinDroite;
	private boolean passif = false;
	private final int ID;

	private ActeurNoeud(int id){
		//On créra sûrement un acteurAnneau afin d'init tout
		//this.voisinDroite = getConetxt().actorOf(ActeurNoeud.props(n-1));
		this.ID = id;
	}

	public static Props props(int n){
		props.create(ActeurNoeud.class, n)
	}

	public interface Message{}

	public static class Tour implements Message{
		
	}
   }

   public class Anneau extends AbstarctActor{
	private ACtorSystem system;
	private final List<ActorRef> noeud = new ArrayList<>();
	
	private Anneau(int nb){
		List<int> id = new ArrayList<>()
		for(int i=0; i<nb; i++) id.add(i);
		Collections.shuffle(id);
		for(int i=0; i<id.size(); i++){
			 ActorRef noeud = getContext().actorOf(ActeurNoeud.props(id.get(i)));
			 this.noeud.add(noeud);
		}
   	}
   }

*/
