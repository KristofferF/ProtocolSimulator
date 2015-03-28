package protocolsimulator;

import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Simulates the layers above and below the transport layer
 * 
 * @author Thomas Ejnefjäll
 * @version 2008-02-14
 */
public class Simulator implements LayerSimulator {
	private GUI mLoggWindow;
	private List<Event> mEvents = new Vector<Event>();
	private long mTimeBetweenMessages = 700, mTimeBetweenAandB = 1000;
	private long mLastPacketTime = 0, mLastMessageTime = 0;
	private boolean mContinue = true;
	private List<String> mMessagesToSend = null;
	private List<String> mReceivedMessages = new Vector<String>();
	private Input mSettings = null;
	private TransportLayer mA, mB;
	private Random mRandom = new Random();

	/**
	 * Constructs a Simulator
	 * 
	 * @param input
	 *            User input for the simulation
	 */
	public Simulator(Input input) {
		mSettings = input;
		mLoggWindow = GUI.getInstance();
		mA = new TransportLayer("A", this, input.timerValue, input.windowSize);
		mB = new TransportLayer("B", this, input.timerValue, input.windowSize);
		mMessagesToSend = this.generateMessages(input.numberOfMessages);
	}

	/**
	 * Starts the simulation
	 */
	public void startSimulation() {
		// Start simulation as a thread
		new Thread(new Runnable() {
			public void run() {
				simulate();
			}
		}).start();
	}

	private void simulate() {
		mLoggWindow.clearLogg();
		mLoggWindow.addToLogg("** Simulation started **");

		long currentTime = System.currentTimeMillis();

		mLastMessageTime = currentTime;
		this.insertEvent(new Event(Event.EventType.ApplicationLayer,
				mLastMessageTime, null));

		while (mContinue) {
			currentTime = System.currentTimeMillis();

			while (mEvents.size() > 0 && mEvents.get(0).eventTime < currentTime) {
				this.handleEvents(mEvents.remove(0));
			}
			mLoggWindow.animate(mMessagesToSend, mReceivedMessages,
					getEventsToAnimate(), currentTime);

			this.generateMessageEvent(currentTime);

			// Don't need to use 100 % of CPU
			try {
				Thread.sleep(20);
			} catch (InterruptedException ignore) { /* Do nothing */
			}
		}
		this.print("** Simlation ended **");
	}

	private void generateMessageEvent(long currentTime) {
		if (--mSettings.numberOfMessages > 0) {
			mLastMessageTime += mTimeBetweenMessages;
			this.insertEvent(new Event(Event.EventType.ApplicationLayer,
					mLastMessageTime, null));
		}
	}

	private List<Event> getEventsToAnimate() {
		List<Event> animatedEvents = new Vector<Event>();

		for (Event e : mEvents) {
			if (e.eventType == Event.EventType.NetworkLayer) {
				animatedEvents.add(e);
			}
		}
		return animatedEvents;
	}

	private void handleEvents(Event event) {
		if (event.eventType == Event.EventType.ApplicationLayer) {
			mA.toTransportLayer(mMessagesToSend.remove(0));
		} else if (event.eventType == Event.EventType.NetworkLayer
				&& !event.extendedSegment.isLost) {
			if (event.extendedSegment.segment.from.equals("A")) {
				mB.toTransportLayer(event.extendedSegment.segment);
			} else if (event.extendedSegment.segment.from.equals("B")) {
				mA.toTransportLayer(event.extendedSegment.segment);
			}
		} else if (event.eventType == Event.EventType.TimerInterrupt) {
			mA.timerInterrupt();
		}
	}

	/**
	 * Stops the simulation
	 */
	public void stopSimulation() {
		mContinue = false;
	}

	@Override
	public void print(String text) {
		mLoggWindow.addToLogg(text);
	}

	@Override
	public void startTimer(int time) {
		this.resetTimer();
		long eventTime = System.currentTimeMillis() + time;
		this.insertEvent(new Event(Event.EventType.TimerInterrupt, eventTime,
				null));
	}

	@Override
	public boolean isTimerActive() {
		boolean isActive = false;

		for (Event e : mEvents) {
			if (e.eventType == Event.EventType.TimerInterrupt) {
				isActive = true;
				break;
			}
		}
		return isActive;
	}

	@Override
	public void resetTimer() {
		for (Event e : mEvents) {
			if (e.eventType == Event.EventType.TimerInterrupt) {
				mEvents.remove(e);
				break;
			}
		}
	}

	@Override
	public void toApplicationLayer(String message) {
		mReceivedMessages.add(message);
	}

	@Override
	public void toNetworkLayer(Segment segment) {
		long time = System.currentTimeMillis() + mTimeBetweenAandB;
		ExtendedSegment extendedSegment = null;
		Segment clone = segment.clone();

		// Make sure last packet from transport layer already
		// has been sent into the channel
		if (time < mLastPacketTime + 100) {
			time = mLastPacketTime + 100;
		}
		mLastPacketTime = time;

		boolean isCorrupted = isAffected(mSettings.corruptionProbability);

		if (isCorrupted) {
			clone = this.corrupt(clone);
		}
		extendedSegment = new ExtendedSegment(clone, isCorrupted, isAffected(mSettings.lossProbability));

		this.insertEvent(new Event(Event.EventType.NetworkLayer, time, extendedSegment));
	}

	private Segment corrupt(Segment segment) {
		int random = mRandom.nextInt(4);

		switch (random) {
		case 0:
			segment.payload = "***";
			break;
		case 1:
			segment.ackNumber = segment.ackNumber + mRandom.nextInt();
			break;
		case 2:
			segment.checksum = segment.checksum + mRandom.nextInt();
			break;
		case 3:
			segment.seqNumber = segment.seqNumber + mRandom.nextInt();
			break;
		}
		return segment;
	}

	private boolean isAffected(int probability) {
		boolean affected = false;

		if (probability >= mRandom.nextInt(100) + 1) {
			affected = true;
		}
		return affected;
	}

	private List<String> generateMessages(int numberOfMessages) {
		String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String message = "AAA";
		Vector<String> messages = new Vector<String>();

		for (int i = 0, m = 0; i < numberOfMessages; i++) {
			m = i % letters.length();
			message = message.replaceAll(message.substring(0, 1),
					letters.substring(m, m + 1));
			messages.add(message);
		}
		return messages;
	}

	private void insertEvent(Event event) {
		boolean inserted = false;

		for (int i = 0; i < mEvents.size() && !inserted; i++) {
			if (event.eventTime < mEvents.get(i).eventTime) {
				mEvents.add(i, event);
				inserted = true;
			}
		}
		if (!inserted) {
			mEvents.add(mEvents.size(), event);
		}
	}
}