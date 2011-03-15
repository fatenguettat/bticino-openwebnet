package it.incalza.myhome;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

public class TestGeneric
{

	@Test
	public void testListButton()
	{
		List<String> buttonsLast = new ArrayList<String>();
		List<String> buttons = new ArrayList<String>();
		List<String> buttonsTemp = new ArrayList<String>();
		buttons = getListTest1();
		buttonsTemp = buttons;
		System.out.println("BUTTONS: " +ArrayUtils.toString(buttons.toArray(new String[buttons.size()])));
		if (buttonsTemp.removeAll(buttonsLast))
		{
			System.out.println("REMOVED: " +ArrayUtils.toString(buttonsLast.toArray(new String[buttonsLast.size()])));
		}
		buttonsLast = buttons;
		buttons = getListTest2();
		buttonsTemp = buttons;
		System.out.println("BUTTONS: " +ArrayUtils.toString(buttons.toArray(new String[buttons.size()])));
		if (buttonsTemp.removeAll(buttonsLast))
		{
			System.out.println("REMOVED: " +ArrayUtils.toString(buttonsLast.toArray(new String[buttonsLast.size()])));
		}
		buttonsLast = buttons;
		buttons = getListTest3();
		buttonsTemp = buttons;
		System.out.println("BUTTONS: " +ArrayUtils.toString(buttons.toArray(new String[buttons.size()])));
		if (buttonsTemp.removeAll(buttonsLast))
		{
			System.out.println("REMOVED: " +ArrayUtils.toString(buttonsLast.toArray(new String[buttonsLast.size()])));
		}
		buttonsLast = buttons;
		buttons = getListTest1();
		buttonsTemp = buttons;
		System.out.println("BUTTONS: " +ArrayUtils.toString(buttons.toArray(new String[buttons.size()])));
		if (buttonsTemp.removeAll(buttonsLast))
		{
			System.out.println("REMOVED: " +ArrayUtils.toString(buttonsLast.toArray(new String[buttonsLast.size()])));
		}
		buttonsLast = buttons;
	}
	
	
	private List<String> getListTest1()
	{
		List<String> buttons = new ArrayList<String>();
		buttons.add("BUTTON_1");
		buttons.add("BUTTON_2");
		buttons.add("BUTTON_3");
		buttons.add("BUTTON_4");
		return buttons;
	}
	
	private List<String> getListTest2()
	{
		List<String> buttons = new ArrayList<String>();
		buttons.add("BUTTON_1");
		buttons.add("BUTTON_2");
		buttons.add("BUTTON_4");
		return buttons;
	}
	
	private List<String> getListTest3()
	{
		List<String> buttons = new ArrayList<String>();
		buttons.add("BUTTON_1");
		buttons.add("BUTTON_2");
		buttons.add("BUTTON_3");
		buttons.add("BUTTON_4");
		buttons.add("BUTTON_6");
		buttons.add("BUTTON_5");
		return buttons;
	}
	
}
