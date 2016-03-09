package org.robolectric.res;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.R;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.TestRunners;
import org.robolectric.shadows.ShadowApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.robolectric.RuntimeEnvironment.application;
import static org.robolectric.Shadows.shadowOf;
import static org.robolectric.util.TestUtil.TEST_PACKAGE;
import static org.robolectric.util.TestUtil.assertInstanceOf;
import static org.robolectric.util.TestUtil.systemResources;
import static org.robolectric.util.TestUtil.testResources;

@RunWith(TestRunners.WithDefaults.class)
public class DrawableResourceLoaderTest {
  protected DrawableResourceLoader drawableResourceLoader;
  private ResBundle<DrawableNode> drawableNodes;
  private Resources resources;

  @Before
  public void setup() throws Exception {
    drawableNodes = new ResBundle<>();
    drawableResourceLoader = new DrawableResourceLoader(drawableNodes);
    new DocumentLoader(testResources()).load("drawable", drawableResourceLoader);
    new DocumentLoader(testResources()).load("anim", drawableResourceLoader);
    new DocumentLoader(systemResources()).load("drawable", drawableResourceLoader);

    drawableResourceLoader.findDrawableResources(testResources());
    drawableResourceLoader.findDrawableResources(systemResources());
    resources = RuntimeEnvironment.application.getResources();
  }

  @Test
  public void testProcessResourceXml() throws Exception {
    drawableNodes = new ResBundle<>();
    drawableResourceLoader = new DrawableResourceLoader(drawableNodes);

    new DocumentLoader(testResources()).load("drawable", drawableResourceLoader);
    drawableResourceLoader.findDrawableResources(testResources());

    assertNotNull(drawableNodes.get(new ResName(TEST_PACKAGE, "drawable", "rainbow"), ""));
    assertEquals(19, drawableNodes.size());
  }

  @Test
  public void testGetDrawable_rainbow() throws Exception {
    assertNotNull(RuntimeEnvironment.application.getResources().getDrawable(R.drawable.rainbow));
  }

  @Test
  public void testGetDrawable_shouldWorkWithSystem() throws Exception {
    assertNotNull(resources.getDrawable(android.R.drawable.ic_popup_sync));
  }

  @Test
  public void testGetDrawable_red() throws Exception {
    assertNotNull(Resources.getSystem().getDrawable(android.R.drawable.ic_menu_help));
  }

  @Test
  public void testDrawableTypes() {
    assertThat(resources.getDrawable(R.drawable.l7_white)).isInstanceOf(BitmapDrawable.class);
    assertThat(resources.getDrawable(R.drawable.l0_red)).isInstanceOf(BitmapDrawable.class);
    assertThat(resources.getDrawable(R.drawable.nine_patch_drawable)).isInstanceOf(NinePatchDrawable.class);
    assertThat(resources.getDrawable(R.drawable.rainbow)).isInstanceOf(LayerDrawable.class);
  }

  @Test
  public void testLayerDrawable() {
    Resources resources = RuntimeEnvironment.application.getResources();
    Drawable drawable = resources.getDrawable(R.drawable.rainbow);
    assertThat(drawable).isInstanceOf(LayerDrawable.class);
    assertEquals(8, ((LayerDrawable) drawable).getNumberOfLayers());

    shadowOf(resources.getAssets()).setQualifiers("xlarge");

    assertEquals(6, ((LayerDrawable) resources.getDrawable(R.drawable.rainbow)).getNumberOfLayers());
  }

  @Test
  public void shouldCreateAnimators() throws Exception {
    Animator animator = AnimatorInflater.loadAnimator(application, R.animator.spinning);
    assertInstanceOf(Animator.class, animator);
  }

  @Test
  public void shouldCreateAnimsAndColors() throws Exception {
    assertInstanceOf(ColorDrawable.class, resources.getDrawable(R.color.grey42));
  }
}
