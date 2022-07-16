package panels;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import main.GuiLogic;
import main.Task;
import xGui.XBorderPanel;
import xGui.XButton;
import xGui.XLabel;
import xGui.XScrollPanel;

public class TaskListPanel extends XBorderPanel {
	
	private static final long serialVersionUID = 1L;

	private List<Task> tasks = new ArrayList<>();
	
	private XScrollPanel tasksPanel = new XScrollPanel();
	
	public TaskListPanel() {
		super();
		GuiLogic.getInstance().registerTaskList(this);
		center.setLayout(new BorderLayout());
		center.add(tasksPanel, BorderLayout.CENTER);
		
		north.add(new XButton("Execute all", e -> executeAll()));
		north.add(new XButton("Delete all", e -> deleteAll()));
		north.add(new XButton("Select all", e -> selectAll(true)));
		north.add(new XButton("Deselect all", e -> selectAll(false)));
	}
	
	private void selectAll(boolean selected) {
		for (Task task : tasks) {
			task.setSelected(selected);
		}
	}
	
	private void updateIndixes() {
		for (int i = 0; i < tasks.size(); i++) {
			tasks.get(i).setIndex(i);
		}
	}
	
	private void deleteAll() {
		for (Task task : tasks) {
			tasksPanel.content.remove(task);
		}
		tasks.clear();
		revalidate();
		repaint();
	}
	
	public void executeAll() {
		execute(0);
	}
	
	private void execute(int index) {
		if(index >= tasks.size()) return;
		tasks.get(index).start(succsess -> {
			if(succsess)
				execute(index + 1);
			else
				north.add(new XLabel("One task failed see console for more details"));
		});
	}
	
	public void addTask(Task t) {
		tasks.add(t);
		tasksPanel.content.add(t);
		t.taskList = this;
		updateIndixes();
		revalidate();
		repaint();
	}
	
	public void removeTask(Task t) {
		tasks.remove(t);
		tasksPanel.content.remove(t);
		updateIndixes();
		revalidate();
		repaint();
	}
}