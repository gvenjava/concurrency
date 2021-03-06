package com.ch3.phaser.website;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;

public class PhaserExample {

	public static void main(String[] args) throws InterruptedException {

		List<Runnable> tasks = new ArrayList<>();

		for (int i = 0; i < 2; i++) {

			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					System.out.printf("THREAD NAME : %s%n", Thread
							.currentThread().getName());
					int a = 0, b = 1;
					for (int i = 0; i < 2000000000; i++) {
						a = a + b;
						b = a - b;
					}
				}
			};

			tasks.add(runnable);

		}

		new PhaserExample().runTasks(tasks);

	}

	void runTasks(List<Runnable> tasks) throws InterruptedException {

		final Phaser phaser = new Phaser(1) {
			protected boolean onAdvance(int phase, int registeredParties) {
				System.out.println("AFTER arriveAndDeregister ::: PHASE = "
						+ phase + " REGISTERED PARTIES " + registeredParties);
				return phase >= 1 || registeredParties == 0;
				// return true;
			}
		};

		for (final Runnable task : tasks) {
			phaser.register();

			new Thread() {
				public void run() {
					do {
						phaser.arriveAndAwaitAdvance();

						task.run();
					} while (!phaser.isTerminated());
				}
			}.start();

		}

		phaser.arriveAndDeregister();
	}

}
