/*
 * Copyright 2009 JBoss, a division of Red Hat Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.errai.demo.summit2013.client.local;

import javax.inject.Inject;

import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.demo.summit2013.client.shared.UserComplaint;
import org.jboss.errai.demo.summit2013.client.shared.UserComplaintEndpoint;
import org.jboss.errai.enterprise.client.jaxrs.api.ResponseCallback;
import org.jboss.errai.ui.client.widget.ValueImage;
import org.jboss.errai.ui.nav.client.local.DefaultPage;
import org.jboss.errai.ui.nav.client.local.Page;
import org.jboss.errai.ui.nav.client.local.TransitionAnchor;
import org.jboss.errai.ui.nav.client.local.TransitionTo;
import org.jboss.errai.ui.shared.api.annotations.Bound;
import org.jboss.errai.ui.shared.api.annotations.DataField;
import org.jboss.errai.ui.shared.api.annotations.EventHandler;
import org.jboss.errai.ui.shared.api.annotations.Model;
import org.jboss.errai.ui.shared.api.annotations.Templated;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.googlecode.gwtphonegap.client.camera.Camera;
import com.googlecode.gwtphonegap.client.camera.PictureCallback;
import com.googlecode.gwtphonegap.client.camera.PictureOptions;

@Page(role = DefaultPage.class)
@Templated("Complaint.html#app-template")
public class ComplaintEntry extends Composite {

  @Inject
  @Model
  private UserComplaint model;

  @Inject
  @Bound
  @DataField
  private TextBox name;

  @Inject
  @Bound
  @DataField
  private TextBox email;

  @Inject
  @Bound
  @DataField
  private TextArea complaint;

  @Inject
  @DataField
  private Button submit;

  @Inject
  @DataField
  private TransitionAnchor<Admin> admin;

  @Inject
  private Caller<UserComplaintEndpoint> endpoint;

  @Inject
  private TransitionTo<ComplaintSubmitted> complaintSubmittedPage;

  @Inject
  private Camera camera;

  @Inject
  @DataField
  private Button takePicture;

  @Inject
  @Bound
  @DataField
  private ValueImage image;

  @EventHandler("submit")
  private void onSubmit(ClickEvent e) {
    endpoint.call(new ResponseCallback() {
      @Override
      public void callback(Response response) {
        complaintSubmittedPage.go();
      }
    }).create(model);
  }

  @EventHandler("takePicture")
  private void onTakePictureClick(ClickEvent e) {
    PictureOptions options = new PictureOptions(25);
    options.setDestinationType(PictureOptions.DESTINATION_TYPE_DATA_URL);
    options.setSourceType(PictureOptions.PICTURE_SOURCE_TYPE_CAMERA);

    camera.getPicture(options, new PictureCallback() {

      @Override
      public void onSuccess(String data) {
        image.setVisible(true);
        image.setValue("data:image/jpeg;base64," + data, true);
      }

      @Override
      public void onFailure(String error) {
        Window.alert("Could not take picture: " + error);
      }
      });
   }
}
