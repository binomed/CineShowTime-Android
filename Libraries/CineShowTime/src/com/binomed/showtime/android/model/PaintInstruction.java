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

import android.graphics.Paint;

public class PaintInstruction {

	private String textToPrint;

	private Paint painter;

	private int posX;

	private int posY;

	private long timeUse;

	public void valuate(String textToPrint, Paint painter, int posX, int posY) {
		this.textToPrint = textToPrint;
		this.painter = painter;
		this.posX = posX;
		this.posY = posY;
	}

	public String getTextToPrint() {
		return textToPrint;
	}

	public Paint getPainter() {
		return painter;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setTimeUse(long timeUse) {
		this.timeUse = timeUse;
	}

	public long getTimeUse() {
		return timeUse;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((painter == null) ? 0 : painter.hashCode());
		result = prime * result + posX;
		result = prime * result + posY;
		result = prime * result + ((textToPrint == null) ? 0 : textToPrint.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PaintInstruction other = (PaintInstruction) obj;
		if (painter == null) {
			if (other.painter != null) {
				return false;
			}
		} else if (!painter.equals(other.painter)) {
			return false;
		}
		if (posX != other.posX) {
			return false;
		}
		if (posY != other.posY) {
			return false;
		}
		if (textToPrint == null) {
			if (other.textToPrint != null) {
				return false;
			}
		} else if (!textToPrint.equals(other.textToPrint)) {
			return false;
		}
		return true;
	}

}
