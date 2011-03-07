package it.incalza.myhome.input.controller;

import java.util.List;

public interface InputControllerHandler
{
	public void handleEventPressed(String action);
	public void handleEventPressedButton(List<String> buttons);
	
	public void handleEventReleased(String action);
	public void handleEventReleasedButton(List<String> buttons);
}
