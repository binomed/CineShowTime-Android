/*
 * Copyright (C) 2011 Binomed (http://blog.binomed.fr)
 *
 * Licensed under the Eclipse Public License - v 1.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.eclipse.org/legal/epl-v10.html
 *
 * THE ACCOMPANYING PROGRAM IS PROVIDED UNDER THE TERMS OF THIS ECLIPSE PUBLIC 
 * LICENSE ("AGREEMENT"). ANY USE, REPRODUCTION OR DISTRIBUTION OF THE PROGRAM 
 * CONSTITUTES RECIPIENT'S ACCEPTANCE OF THIS AGREEMENT.
 */
package com.binomed.showtime.android.model;

import java.util.ArrayList;

import android.graphics.Paint;

public class PaintInstructionPool {

	private static PaintInstructionPool instance;

	public static synchronized PaintInstructionPool getInstance() {
		if (instance == null) {
			instance = new PaintInstructionPool();
		}
		return instance;
	}

	private long expirationTime;

	private ArrayList<PaintInstruction> locked, unlocked;

	private PaintInstructionPool() {
		expirationTime = 30000; // 30 seconds
		locked = new ArrayList<PaintInstruction>(500);
		unlocked = new ArrayList<PaintInstruction>(500);
	}

	public synchronized PaintInstruction checkOut() {
		long now = System.currentTimeMillis();
		PaintInstruction t;
		if (unlocked.size() > 0) {
			for (int i = 0; i < unlocked.size(); i++) {
				t = unlocked.get(i);
				if ((now - t.getTimeUse()) > expirationTime) {
					// object has expired
					unlocked.remove(i);
					expire(t);
					t = null;
					i = 0;
				} else {
					unlocked.remove(i);
					t.setTimeUse(now);
					locked.add(t);
					return (t);
				}
			}
		}
		// no objects available, create a new one
		t = create();
		t.setTimeUse(now);
		locked.add(t);
		return (t);
	}

	public synchronized void checkIn(PaintInstruction t) {
		locked.remove(t);
		t.setTimeUse(System.currentTimeMillis());
		unlocked.add(t);
	}

	public synchronized PaintInstruction newInstance(String textToPrint, Paint painter, int posX, int posY) {
		PaintInstruction instruction = checkOut();
		instruction.valuate(textToPrint, painter, posX, posY);
		return instruction;
	}

	protected PaintInstruction create() {
		return new PaintInstruction();
	}

	public void expire(PaintInstruction o) {
		o.valuate(null, null, 0, 0);
	}

}
