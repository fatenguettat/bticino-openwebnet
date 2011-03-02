/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.incalza.myhome.input.controller;


import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

/**
 *
 * @author Adnan
 */
public class InputController implements Runnable {

	public static final float SENSITIVITY = 0.9f;
    private final int SLEEP_TIME = 20; //in ms
    private EventQueue mQueue;
    private Event mEvent;
    private Controller mMyController = null;
    private Main application = null;
    private Event mPreviousEvent = null;
    private int counter = 0;
    private volatile boolean mAlive = true;
    Thread mThread;

    /**
     * 
     * @param aMyController
     */
    public InputController(Main application, Controller aMyController) {
        this.application = application;
        this.mMyController = aMyController;
    }

    public void run() {
        try {
            while (mAlive) {
                if (mMyController != null) {
                    try {
                        mMyController.poll();
                    } catch (Exception e) {
                        counter++;
                    }
                    mQueue = mMyController.getEventQueue();
                    mEvent = new Event();
                    if (mQueue.getNextEvent(mEvent)) {
                        Component lComp = mEvent.getComponent();
                        float lValue = mEvent.getValue();
                        if (lValue >= SENSITIVITY) {
                            lValue = 1.0f;
                        }
                        if (lValue <= -SENSITIVITY) {
                            lValue = -1.0f;
                        }
                        if (lValue == 1.0 || lValue == -1.0) {
//                            if (lComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().MOVE_LEFT]) ||
//                                    lComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().MOVE_RIGHT]) ||
//                                    (mFighter.isGotGoofyBoots() &&
//                                    (lComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().JUMP2]) ||
//                                    lComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().JUMP1]) ||
//                                    lComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().CROUCH])))) {
//                                mPreviousEvent = mEvent;
//                            }
                            while (mPreviousEvent != null /*&&
                                    (mPreviousEvent.getComponent().getName().equals(mFighter.getControls()[mFighter.getMInputMap().MOVE_LEFT]) ||
                                    mPreviousEvent.getComponent().getName().equals(mFighter.getControls()[mFighter.getMInputMap().MOVE_RIGHT]) ||
                                    (mFighter.isGotGoofyBoots() &&
                                    (mPreviousEvent.getComponent().getName().equals(mFighter.getControls()[mFighter.getMInputMap().JUMP1]) ||
                                    mPreviousEvent.getComponent().getName().equals(mFighter.getControls()[mFighter.getMInputMap().JUMP2]) ||
                                    mPreviousEvent.getComponent().getName().equals(mFighter.getControls()[mFighter.getMInputMap().CROUCH]))))*/) {
                                application.handleEventPressed(lComp.getName(), lValue, lComp.isAnalog());

                                try {
                                    mMyController.poll();
                                } catch (Exception e) {
                                    counter++;
                                }
                                mQueue = mMyController.getEventQueue();
                                mEvent = new Event();
                                if (mQueue.getNextEvent(mEvent)) {
                                    float lAnotherValue = mEvent.getValue();
                                    Component lAnotherComp = mEvent.getComponent();

                                    if (lAnotherValue >= SENSITIVITY) {
                                        lAnotherValue = 1.0f;
                                    }
                                    if (lAnotherValue <= -SENSITIVITY) {
                                        lAnotherValue = -1.0f;
                                    }
                                    if (lAnotherValue == 1.0f || lAnotherValue == -1.0f) {
//                                        if (lAnotherComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().PUNCH]) ||
//                                                lAnotherComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().KICK])) {
//                                            mFighter.handleEventPressed(lAnotherComp.getName(), lAnotherValue, lAnotherComp.isAnalog());
//                                            Thread.sleep(SLEEP_TIME);
//                                            mFighter.handleEventReleased(lAnotherComp.getName(), lAnotherValue);
//                                        } else {
//                                            mFighter.handleEventPressed(lAnotherComp.getName(), lAnotherValue, lAnotherComp.isAnalog());
//                                            if(lAnotherComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().MOVE_LEFT]) ||
//                                                lAnotherComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().MOVE_RIGHT])                                          )
//                                                lComp = lAnotherComp;
//                                        }
                                    } else {
                                        application.handleEventReleased(lAnotherComp.getName(), lAnotherValue);
//                                        if (lAnotherComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().MOVE_LEFT]) ||
//                                                lAnotherComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().MOVE_RIGHT]) ||
//                                                (mFighter.isGotGoofyBoots() &&
//                                                (lAnotherComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().JUMP2]) ||
//                                                lAnotherComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().JUMP1]) ||
//                                                lAnotherComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().CROUCH])))) {
//                                            mPreviousEvent = null;
//                                        }
                                    }

                                }
                                Thread.sleep(SLEEP_TIME);
                            }
//                            if (lComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().PUNCH]) ||
//                                    lComp.getName().equals(mFighter.getControls()[mFighter.getMInputMap().KICK])) {
//                                mFighter.handleEventPressed(lComp.getName(), lValue, lComp.isAnalog());
//                                Thread.sleep(SLEEP_TIME);
//                                mFighter.handleEventReleased(lComp.getName(), lValue);
//
//                            } else {
//                                mFighter.handleEventPressed(lComp.getName(), lValue, lComp.isAnalog());
//                            }
                        } else {
                            application.handleEventReleased(lComp.getName(), lValue);
                        }

                        Thread.sleep(SLEEP_TIME);

                    }
                    Thread.sleep(SLEEP_TIME);
                } else {
                    System.out.println("OOPS NO CONTROLLER!");
                    break;
                }
            }
        } catch (InterruptedException e) {
            System.out.println("INTERRUPED GAME CONTROLLER");
        }
        System.out.println("GAME CONTROLER IS DEAD");
        this.mMyController = null;
    }

    public void setAlive(boolean aAlive) {
        this.mAlive = aAlive;
        Thread tmpBlinker = mThread;
        mThread = null;
        if (tmpBlinker != null) {
            tmpBlinker.interrupt();
        }
        mQueue = null;
        mEvent = null;
        System.gc();
    }

    public void setThread(Thread aThread) {
        this.mThread = aThread;
    }
}
