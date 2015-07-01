package bannertrain.mediator;

public class MediatorFactory {
	private static Mediator m = new Mediator();

	public static Mediator getMediator(){
		return m;
	}
}
