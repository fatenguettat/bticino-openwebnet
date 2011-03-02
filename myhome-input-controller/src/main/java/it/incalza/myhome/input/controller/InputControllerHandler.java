package it.incalza.myhome.input.controller;

public interface InputControllerHandler
{
	public void handleEventPressed(String action,float aValue, boolean aIsAnalog);

  public void handleEventReleased(String action, float aValue);
}
