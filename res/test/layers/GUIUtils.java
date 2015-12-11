package no.uib.lca092.rtms.gui;

import java.awt.Component;
import java.awt.Container;

import javax.swing.JList;

public class GUIUtils {
	public static JList<?> findJList(Component comp) {
		if (comp instanceof JList<?>)
			return (JList<?>) comp;
		if (comp instanceof Container) {
			Component[] components = ((Container) comp).getComponents();
			for (int i = 0; i < components.length; i++) {
				JList<?> child = findJList(components[i]);
				if (child != null)
					return child;
			}
		}
		return null;
	}
}
