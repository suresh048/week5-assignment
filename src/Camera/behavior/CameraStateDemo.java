/*
 * My little work as mentioned here under focusing more on behavioral aspect of one camera.
 * The same can be implemented for multiple cameras, but as of now it is for one camera on mobile.
 * 
 */

package Camera.behavior;

interface State {
	abstract void stop();
	abstract void start();
	abstract void pause();
	abstract void load();
	default void enterState() {
	}
}

public class CameraStateDemo implements State {

	public void enterState() {
		currentState.enterState();
	}
	public void stop() {
		currentState.stop();
	}

	public void start() {
		currentState.start();
	}

	@Override
	public void pause() {
		currentState.pause();
	}

	@Override
	public void load() {
		currentState.load();
	}

	State stoppedState = new State() {
		@Override
		public void enterState() {
			stop();
			// setIcon(Icon.stopped);
		}
		@Override
		public void stop() {
			// Do nothing
		}

		@Override
		public void start() {
			currentState = capturingState;
			currentState.enterState();
		}

		@Override
		public void pause() {
			// Do nothing, already stopped
		}

		@Override
		public void load() {
			resetToStart();
		}
	};

	State capturingState = new State() {

		public void enterState() {
			start();
			// setIcon(Icon.CAPTURING);
		}
		
		@Override
		public void stop() {
			currentState = stoppedState;
			currentState.enterState();
		}

		@Override
		public void start() {
			// Do nothing
		}

		@Override
		public void pause() {
			stop();
		}

		@Override
		public void load() {
			stop();
			resetToStart();
			start();
		}
	};

	State pausedState = new State() {

		@Override
		public void stop() {
			currentState = stoppedState;
			currentState.enterState();
		}

		@Override
		public void start() {
			currentState = capturingState;
			currentState.enterState();
		}

		@Override
		public void pause() {
			// Do nothing
		}

		@Override
		public void load() {
			resetToStart();
		}
	};

	State loadState = new State() {

		@Override
		public void stop() {
			currentState = stoppedState;
			currentState.enterState();
		}

		@Override
		public void start() {
			// On mechanical transports we have to stop before going into capture mode
			stop();
			currentState = capturingState;
			currentState.enterState();
		}

		@Override
		public void pause() {
			currentState = pausedState;
			currentState.enterState();
		}

		@Override
		public void load() {
			// Do nothing
		}
		
		public void enterState() {
			load();
		}
	};

	State currentState = stoppedState;
	
	// Non-fully-encapsulation version of getState().
	// public State getState() { return currentState; }

	// "mild encapsulation"? version. Only reveal state name.
	// Might make non-public if it is only for diagnostic use.
	
	public String getState() {
		return currentState.getClass().getName();
	}

	// This section shows the legacy, non-patterns way of implementing
	// one of the four methods.
	
	enum StateName { STOPPED, CAPTURING, PAUSED, LOADING }
	StateName currentStateName;
	public void unmaintainableStart() {
		if (currentStateName == StateName.STOPPED) {
			currentStateName = StateName.CAPTURING;
			startPlay();
		} else if (currentStateName == StateName.PAUSED) {
			currentStateName = StateName.CAPTURING;
			resumePlay();
		} else if (currentStateName == StateName.CAPTURING) {
			System.out.println("Already capturing!");
		} else if (currentStateName == StateName.LOADING) {
			System.out.println("Wait a while, OK?");
		}
	}

	// Low level, hardware control
	void startPlay() {}
	void resumePlay() {}
	void resetToStart() {}
	
	// Demo program
	public static void main(String[] args) {
		System.out.println("This code is more about the behavioral aspect of a mobile camera with different functions");
		CameraStateDemo context = new CameraStateDemo();
		System.out.println("Initial state:-- " + context.getState());
		// User presses the Start button
		context.start();
		System.out.println("Current state:-- " + context.getState());
		// User presses the Stop button
		context.stop();
		System.out.println("Current state:-- " + context.getState());
		// You get the idea
		context.load();
		System.out.println("Current state:-- " + context.getState());
	}
}
