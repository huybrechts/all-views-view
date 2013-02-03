package jenkins.plugins.avv;

import hudson.Extension;
import hudson.model.AllView;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Item;
import hudson.model.ItemGroup;
import hudson.model.Job;
import hudson.model.Node;
import hudson.model.TopLevelItem;
import hudson.model.View;
import hudson.model.ViewDescriptor;
import hudson.model.ViewGroup;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.Stapler;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

public class AllViewsView extends View {

	@DataBoundConstructor
	public AllViewsView(String name) {
		super(name);
	}

	protected AllViewsView(String name, ViewGroup owner) {
		super(name, owner);
	}


	@Override
	public Collection<TopLevelItem> getItems() {
		return Hudson.getInstance().getItems();
	}

	@Override
	public boolean contains(TopLevelItem topLevelItem) {
		return true;
	}

	@Override
	public void onJobRenamed(Item item, String s, String s1) {
	}

	@Override
	protected void submit(StaplerRequest staplerRequest) throws IOException, ServletException, Descriptor.FormException {
	}

	@Override
	public Item doCreateItem(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
		return Hudson.getInstance().doCreateItem(req, rsp);
	}
	
	public int getEnabledJobCount() {
		int result = 0;
		for (TopLevelItem item: getItems()) {
			if (((Job) item).isBuildable()) result++;
		}
		return result;
	}
	
	public int getOnlineNodeCount() {
		int result = 0;
		for (Node node: Hudson.getInstance().getNodes()) {
			  if (node.getChannel() != null) result++;
		}
		return result;
	}
	
	public int getBuildingJobCount() {
		int result = 0;
		for (TopLevelItem item: getItems()) {
			if (((Job) item).isBuilding()) result++;
		}
		return result;
	}

	public int getQueuedJobCount() {
		return Hudson.getInstance().getQueue().getItems().length;
	}

	@Extension
	public static final class DescriptorImpl extends ViewDescriptor {
		@Override
		public boolean isInstantiable() {
			return true;
		}

		public String getDisplayName() {
			return "All Views View";
		}
	}

	@Override
	public synchronized void doSubmitDescription( StaplerRequest req, StaplerResponse rsp ) throws IOException, ServletException {
		checkPermission(Hudson.ADMINISTER);

		Hudson.getInstance().setSystemMessage(req.getParameter("description"));
		rsp.sendRedirect(".");
	}
}
