package jorgan.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import jorgan.disposition.Console;
import jorgan.disposition.Element;
import jorgan.disposition.event.OrganAdapter;
import jorgan.disposition.event.OrganListener;
import jorgan.problem.ElementProblems;
import jorgan.problem.Problem;
import jorgan.problem.Severity;
import jorgan.session.OrganSession;
import jorgan.swing.BaseAction;
import spin.Spin;
import bias.Configuration;
import bias.util.MessageBuilder;

/**
 * An action for initiating full screen.
 */
public class FullScreenAction extends BaseAction {

	private static Configuration config = Configuration.getRoot().get(
			FullScreenAction.class);

	private boolean onLoad = false;

	private boolean real = false;

	private Map<String, FullScreen> screens = new HashMap<String, FullScreen>();

	private OrganSession session;

	private ElementProblems problems;

	private OrganFrame frame;

	private WindowAdapter windowAdapter;

	public FullScreenAction(OrganSession session, OrganFrame frame) {
		config.read(this);

		this.session = session;
		this.session.getOrgan().addOrganListener(
				(OrganListener) Spin.over(new OrganAdapter() {

					public void elementAdded(Element element) {
						if (element instanceof Console) {
							update();
						}
					}

					public void propertyChanged(Element element, String name) {
						if (element instanceof Console && "screen".equals(name)) {
							update();
						}
					}
				}));

		this.frame = frame;

		this.problems = session.lookup(ElementProblems.class);

		windowAdapter = new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				leaveFullScreen();
			}
		};
		frame.addWindowListener(windowAdapter);

		setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F11, 0));

		update();

		if (isEnabled() && frame.isDisplayable()) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					check();
				}
			});
		}
	}

	public void destroy() {
		leaveFullScreen();

		frame.removeWindowListener(windowAdapter);
	}

	public void setOnLoad(boolean onLoad) {
		this.onLoad = onLoad;
	}

	public void setReal(boolean real) {
		this.real = real;
	}

	public void update() {
		for (Console console : session.getOrgan().getElements(Console.class)) {
			removeProblem(console);

			if (console.showFullScreen()) {
				setEnabled(true);
				return;
			}
		}
		setEnabled(false);
	}

	private void check() {
		if (onLoad) {
			enterFullScreen();
		}
	}

	public void actionPerformed(ActionEvent ev) {
		if (screens.isEmpty()) {
			enterFullScreen();
		}
	}

	private void enterFullScreen() {
		for (Console console : session.getOrgan().getElements(Console.class)) {
			String screen = console.getScreen();
			if (screen == null) {
				continue;
			}

			FullScreen fullScreen;
			try {
				fullScreen = getScreen(screen);
			} catch (IllegalArgumentException ex) {
				addProblem(console);
				continue;
			}

			if (fullScreen != null) {
				fullScreen.addConsole(console);
			}
		}
	}

	private void leaveFullScreen() {
		for (FullScreen fullScreen : screens.values()) {
			fullScreen.dispose();
		}
		screens.clear();
	}

	private FullScreen getScreen(String screen) throws IllegalArgumentException {
		FullScreen fullScreen = screens.get(screen);
		if (fullScreen == null) {
			fullScreen = FullScreen.create(session, screen, real);
			fullScreen.addComponentListener(new ComponentAdapter() {
				public void componentHidden(ComponentEvent ev) {
					leaveFullScreen();
				}
			});

			screens.put(screen, fullScreen);
		}
		return fullScreen;
	}

	private void addProblem(Console console) {
		String message = config.get("screenInvalid").read(new MessageBuilder())
				.build(console.getScreen());

		problems.addProblem(new Problem(Severity.ERROR, console, "screen",
				message));
	}

	private void removeProblem(Console console) {
		problems.removeProblem(new Problem(Severity.ERROR, console, "screen",
				null));
	}
}
