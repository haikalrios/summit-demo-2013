package org.jboss.errai.demo.summit2013.client.local;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.jboss.errai.common.client.api.RemoteCallback;
import org.jboss.errai.common.client.util.LogUtil;
import org.jboss.errai.demo.summit2013.client.shared.UserComplaint;
import org.jboss.errai.ioc.client.api.AfterInitialization;
import org.jboss.errai.jpa.sync.client.local.ClientSyncManager;
import org.jboss.errai.jpa.sync.client.shared.SyncResponse;
import org.jboss.errai.ui.client.widget.ListWidget;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.PageShown;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.user.client.ui.Composite;

@Page
@Templated("Admin.html#app-template")
public class Admin extends Composite {
  @DataField
  private ListWidget<UserComplaint, ComplaintWidget> complaints;

  @Inject
  private EntityManager em;

  @Inject
  private App app;

  @Inject
  private ClientSyncManager syncManager;

  public static boolean init;
  
  public Admin() {
    complaints = new ComplaintListWidget("tbody");
  }

  private void loadComplaints() {
    TypedQuery<UserComplaint> query = em.createNamedQuery("allComplaints", UserComplaint.class);
    complaints.setItems(query.getResultList());
  }


  @AfterInitialization
  private void sync() {
    Admin.init = true;
    app.sync(new RemoteCallback<List<SyncResponse<UserComplaint>>>() {
      @Override
      public void callback(List<SyncResponse<UserComplaint>> response) {
        LogUtil.log("Received sync response:" + response);
        loadComplaints();
        flushToLocalStorage();
      }
    });
  }
  
  @PageShown
  private void pageShown() {
    if (init) {
      sync();
    }
  }
  
  private void flushToLocalStorage() {
    // TODO should be done by data sync manager internally
    syncManager.getDesiredStateEm().flush();
    syncManager.getDesiredStateEm().clear();
    syncManager.getExpectedStateEm().flush();
    syncManager.getExpectedStateEm().clear();
  }
  
  private void mergeInLocalStorage(UserComplaint created) {
    syncManager.getExpectedStateEm().merge(created);
    syncManager.getExpectedStateEm().flush();
    syncManager.getDesiredStateEm().merge(created);
    syncManager.getDesiredStateEm().flush();
  }
}
