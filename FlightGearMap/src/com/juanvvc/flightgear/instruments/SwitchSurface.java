package com.juanvvc.flightgear.instruments;

import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.juanvvc.flightgear.FGFSConnection;
import com.juanvvc.flightgear.PlaneData;
import com.juanvvc.flightgear.MyLog;

public class SwitchSurface extends Surface {
	private String prop;
	private String label;
	private boolean state2 = true;
	private Paint textPaint;
	
	private static final int SWITCH_HEIGHT = 152;
	private static final int SWITCH_WIDTH = 126;
	
	private boolean firstRead;
	
	/** If true, the switch needs to be post to the remote fgfs (do not read) */
	private boolean dirty = false;
	
	public SwitchSurface(final String file, final float x, final float y, final String prop, final String label) {
		super(file, x, y);
		this.label = label;
		this.prop = prop;
		firstRead = true;
		
		textPaint = new Paint();
		textPaint.setColor(0xffffffff);
		
		MyLog.d(this, "Creating new " + label);
	}
	
	@Override
	public boolean youControl(float x, float y) {
		return x > this.x && x < (this.x + SWITCH_WIDTH) && y > this.y && y < (this.y + SWITCH_HEIGHT);
	}
	
	@Override
	public void onMove(float x, float y, boolean end) {
		if (end) {
			MyLog.d(this, "Switching " + this.label);
			setState(!getState());
		}
	}
	
	public void setState(boolean s) {
		this.state2 = s;
		MyLog.d(this, label + " sets state to " + this.state2);
		dirty = true;
	}
	
	public boolean getState() {
		return state2;
	}
	
	@Override
	public void onDraw(Canvas c, Bitmap b) {
		// calculate the position of the switch
		final float gridSize = parent.getGridSize();
		final float scale = parent.getScale();
		final float col = parent.getCol();
		final float row = parent.getRow();
		final int left = (int)((x / 512f + col) * gridSize * scale);
		final int top = (int)((y / 512f + row) * gridSize * scale);
		
		// draw the label
		c.drawText(label, left + b.getWidth() / 5 * scale, top + b.getHeight() / 2 * scale, textPaint);
		// draw the switch according to its state
		if (getState()) {
			c.drawBitmap(b,
					new Rect(0, 0, b.getWidth(), b.getHeight() / 2),
					new Rect(left, top, (int)(left + b.getWidth() * scale), (int)(top + b.getHeight() / 2 * scale)),
					null);		
		} else {
			c.drawBitmap(b,
				new Rect(0, b.getHeight() / 2, b.getWidth(), b.getHeight()),
				new Rect(left, top, (int)(left + b.getWidth() * scale), (int)(top + b.getHeight() / 2 * scale)),
				null);
		}
		
	}
	
	@Override
	public void postCalibratableSurfaceManager(CalibratableSurfaceManager cs) {
		cs.register(this);
	}

	@Override
	public boolean isDirty() {
		return dirty || firstRead;
	}

	@Override
	public void update(FGFSConnection conn) throws IOException {
		if (conn == null || conn.isClosed()) {
			return;
		}
		// if not moving, just read from the remote connection
		if (dirty) {
			// if dirty, post the state of the switch
			MyLog.i(this, "Updating: " + label + " " + getState());
			conn.setBoolean(this.prop, getState());
			dirty = false;
		} else {
			this.setState(conn.getBoolean(this.prop));
			// TODO: reading the state from the remote fgfs is SLOW.
			// We only read once after creating the switch.
			// So, if the user changes the state in the remote fgfs, we will never find out.
			firstRead = false;
		}
	}
}

