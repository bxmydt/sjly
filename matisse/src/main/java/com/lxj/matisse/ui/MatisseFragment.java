/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lxj.matisse.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.lxj.matisse.CaptureMode;
import com.lxj.matisse.MatisseConst;
import com.lxj.matisse.R;
import com.lxj.matisse.internal.entity.Album;
import com.lxj.matisse.internal.entity.Item;
import com.lxj.matisse.internal.entity.SelectionSpec;
import com.lxj.matisse.internal.model.SelectedItemCollection;
import com.lxj.matisse.internal.ui.AlbumPreviewActivity;
import com.lxj.matisse.internal.ui.BasePreviewActivity;
import com.lxj.matisse.internal.ui.MediaSelectionFragment;
import com.lxj.matisse.internal.ui.SelectedPreviewActivity;
import com.lxj.matisse.internal.ui.adapter.AlbumMediaAdapter;
import com.lxj.matisse.internal.ui.widget.CheckRadioView;
import com.lxj.matisse.internal.ui.widget.IncapableDialog;
import com.lxj.matisse.internal.utils.MediaStoreCompat;
import com.lxj.matisse.internal.utils.PathUtils;
import com.lxj.matisse.internal.utils.PhotoMetadataUtils;
import com.lxj.xpermission.PermissionConstants;
import com.lxj.xpermission.XPermission;
import com.yalantis.ucrop.UCrop;
import com.zpj.fragmentation.BaseFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Main Activity to display albums and media content (images/videos) in each album
 * and also support media selecting operations.
 */
public class MatisseFragment extends BaseFragment implements
        MediaSelectionFragment.SelectionProvider, View.OnClickListener,
        AlbumMediaAdapter.CheckStateListener, AlbumMediaAdapter.OnMediaClickListener,
        AlbumMediaAdapter.OnPhotoCapture {

    public static final String CHECK_STATE = "checkState";
    private MediaStoreCompat mMediaStoreCompat;
    private SelectedItemCollection mSelectedCollection;
    private SelectionSpec mSpec;

    private TextView mButtonPreview;
    private TextView mButtonApply;

    private LinearLayout mOriginalLayout;
    private CheckRadioView mOriginal;
    private boolean mOriginalEnable;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_matisse;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        mSelectedCollection = new SelectedItemCollection(context);
        mSpec = SelectionSpec.getInstance();
        if (!mSpec.hasInited) {
            pop();
            return;
        }

        if (mSpec.needOrientationRestriction()) {
            _mActivity.setRequestedOrientation(mSpec.orientation);
        }

        if (mSpec.capture) {
            mMediaStoreCompat = new MediaStoreCompat(_mActivity);
            if (mSpec.captureStrategy == null)
                throw new RuntimeException("Don't forget to set CaptureStrategy.");
            mMediaStoreCompat.setCaptureStrategy(mSpec.captureStrategy);
        }

        mButtonPreview = view.findViewById(R.id.button_preview);
        mButtonApply = view.findViewById(R.id.button_apply);
        mButtonPreview.setOnClickListener(this);
        mButtonApply.setOnClickListener(this);
        mOriginalLayout = view.findViewById(R.id.originalLayout);
        mOriginal = view.findViewById(R.id.original);
        mOriginalLayout.setOnClickListener(this);

        mSelectedCollection.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mOriginalEnable = savedInstanceState.getBoolean(CHECK_STATE);
        }
        updateBottomToolbar();


        AlbumFragment2 albumFragment = findChildFragment(AlbumFragment2.class);
        if (albumFragment == null) {
            albumFragment = new AlbumFragment2();
        }
        albumFragment.setMatisseFragment(this);
        loadRootFragment(R.id.container, albumFragment);
    }

    @Override
    public boolean onBackPressedSupport() {
        if (getChildFragmentManager().getBackStackEntryCount() > 1) {
            popChild();
            return true;
        }
        return super.onBackPressedSupport();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
        outState.putBoolean("checkState", mOriginalEnable);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSpec.onCheckedListener = null;
        mSpec.onSelectedListener = null;
    }

    ArrayList<Uri> selectedUris = new ArrayList<>();
    ArrayList<String> selectedPaths = new ArrayList<>();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == MatisseConst.REQUEST_CODE_PREVIEW) {
            Bundle resultBundle = data.getBundleExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE);
            ArrayList<Item> selected = resultBundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
            mOriginalEnable = data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, false);
            int collectionType = resultBundle.getInt(SelectedItemCollection.STATE_COLLECTION_TYPE,
                    SelectedItemCollection.COLLECTION_UNDEFINED);
            if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {
                Intent result = new Intent();
                selectedUris.clear();
                selectedPaths.clear();
                if (selected != null) {
                    for (Item item : selected) {
                        selectedUris.add(item.getContentUri());
                        selectedPaths.add(PathUtils.getPath(context, item.getContentUri()));
                    }
                }
                result.putParcelableArrayListExtra(MatisseConst.EXTRA_RESULT_SELECTION, selectedUris);
                result.putStringArrayListExtra(MatisseConst.EXTRA_RESULT_SELECTION_PATH, selectedPaths);
                result.putExtra(MatisseConst.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
                if (mSpec.isCrop && selected != null && selected.size() == 1 && selected.get(0).isImage()) {
                    //开启裁剪
                    startCrop(_mActivity, selected.get(0).uri);
                    return;
                }
//                setFragmentResult(RESULT_OK, result);
                pop();
            } else {
                mSelectedCollection.overwrite(selected, collectionType);
                Fragment mediaSelectionFragment = getChildFragmentManager().findFragmentByTag(
                        MediaSelectionFragment.class.getSimpleName());
                if (mediaSelectionFragment instanceof MediaSelectionFragment) {
                    ((MediaSelectionFragment) mediaSelectionFragment).refreshMediaGrid();
                }
                updateBottomToolbar();
            }
        } else if (requestCode == MatisseConst.REQUEST_CODE_CAPTURE) {
            // Just pass the data back to previous calling Activity.
//            setResult(RESULT_OK, data);
//            finish();
            pop();
        } else if (requestCode == UCrop.REQUEST_CROP) {
//            final Uri resultUri = UCrop.getOutput(data);
//            if (resultUri != null) {
//                //finish with result.
//                Intent result = getIntent();
//                result.putExtra(MatisseConst.EXTRA_RESULT_CROP_PATH, resultUri.getPath());
//                ArrayList<Uri> selectedUris = (ArrayList<Uri>) mSelectedCollection.asListOfUri();
//                result.putParcelableArrayListExtra(MatisseConst.EXTRA_RESULT_SELECTION, selectedUris);
//                ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
//                result.putStringArrayListExtra(MatisseConst.EXTRA_RESULT_SELECTION_PATH, selectedPaths);
//                setResult(RESULT_OK, result);
//                finish();
//            } else {
//                Log.e("Matisse", "ucrop occur error: " + UCrop.getError(data).toString());
//            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            Log.e("Matisse", "ucrop occur error: " + UCrop.getError(data).toString());
        }
    }

    private void updateBottomToolbar() {

        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            mButtonPreview.setEnabled(false);
            mButtonApply.setEnabled(false);
            mButtonApply.setText(getString(R.string.button_sure_default));
        } else if (selectedCount == 1 && mSpec.singleSelectionModeEnabled()) {
            mButtonPreview.setEnabled(true);
            mButtonApply.setText(R.string.button_sure_default);
            mButtonApply.setEnabled(true);
        } else {
            mButtonPreview.setEnabled(true);
            mButtonApply.setEnabled(true);
            mButtonApply.setText(getString(R.string.button_sure, selectedCount));
        }


        if (mSpec.originalable) {
            mOriginalLayout.setVisibility(View.VISIBLE);
            updateOriginalState();
        } else {
            mOriginalLayout.setVisibility(View.INVISIBLE);
        }


    }

    private void updateOriginalState() {
        mOriginal.setChecked(mOriginalEnable);
        if (countOverMaxSize() > 0) {

            if (mOriginalEnable) {
                IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.error_over_original_size, mSpec.originalMaxSize));
                incapableDialog.show(getChildFragmentManager(),
                        IncapableDialog.class.getName());

                mOriginal.setChecked(false);
                mOriginalEnable = false;
            }
        }
    }


    private int countOverMaxSize() {
        int count = 0;
        int selectedCount = mSelectedCollection.count();
        for (int i = 0; i < selectedCount; i++) {
            Item item = mSelectedCollection.asList().get(i);

            if (item.isImage()) {
                float size = PhotoMetadataUtils.getSizeInMB(item.size);
                if (size > mSpec.originalMaxSize) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_preview) {
            Intent intent = new Intent(context, SelectedPreviewActivity.class);
            intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
            intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            startActivityForResult(intent, MatisseConst.REQUEST_CODE_PREVIEW);
        } else if (v.getId() == R.id.button_apply) {

            List<Uri> selectedUris =  mSelectedCollection.asListOfUri();
            List<String> selectedPaths = mSelectedCollection.asListOfString();
            if (mSpec.isCrop && selectedPaths.size() == 1 && mSelectedCollection.asList().get(0).isImage()) {
                //start crop
                startCrop(_mActivity, selectedUris.get(0));
            } else {
                if (mSpec.onSelectedListener != null) {
                    mSpec.onSelectedListener.onSelected(selectedUris, selectedPaths);
                }
                pop();
            }
        } else if (v.getId() == R.id.originalLayout) {
            int count = countOverMaxSize();
            if (count > 0) {
                IncapableDialog incapableDialog = IncapableDialog.newInstance("",
                        getString(R.string.error_over_original_count, count, mSpec.originalMaxSize));
                incapableDialog.show(getChildFragmentManager(),
                        IncapableDialog.class.getName());
                return;
            }

            mOriginalEnable = !mOriginalEnable;
            mOriginal.setChecked(mOriginalEnable);

            if (mSpec.onCheckedListener != null) {
                mSpec.onCheckedListener.onCheck(mOriginalEnable);
            }
        }
    }

    public static void startCrop(Activity context, Uri source) {
        String destinationFileName = System.nanoTime() + "_crop.jpg";
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(90);
        // Color palette
        TypedArray ta = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.colorPrimary,
                        R.attr.colorPrimaryDark});
        int primaryColor = ta.getColor(0, Color.TRANSPARENT);
        options.setToolbarColor(primaryColor);
        options.setStatusBarColor(ta.getColor(1, Color.TRANSPARENT));
        options.setActiveWidgetColor(primaryColor);
        ta.recycle();
        File cacheFile = new File(context.getCacheDir(), destinationFileName);
        UCrop.of(source, Uri.fromFile(cacheFile))
                .withAspectRatio(1, 1)
                .withOptions(options)
                .start(context);
    }

    @Override
    public void onUpdate() {
        // notify bottom toolbar that check state changed.
        updateBottomToolbar();

//        if (mSpec.onSelectedListener != null) {
//            mSpec.onSelectedListener.onSelected(
//                    mSelectedCollection.asListOfUri(), mSelectedCollection.asListOfString());
//        }
    }

    @Override
    public void onMediaClick(Album album, Item item, int adapterPosition) {
        Intent intent = new Intent(context, AlbumPreviewActivity.class);
        intent.putExtra(AlbumPreviewActivity.EXTRA_ALBUM, album);
        intent.putExtra(AlbumPreviewActivity.EXTRA_ITEM, item);
        intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
        intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
        startActivityForResult(intent, MatisseConst.REQUEST_CODE_PREVIEW);
    }

    @Override
    public SelectedItemCollection provideSelectedItemCollection() {
        return mSelectedCollection;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void capture() {
//        if (mMediaStoreCompat != null) {
//            mMediaStoreCompat.dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE);
//        }


        String[] permissions = mSpec.captureMode == CaptureMode.Image ? new String[]{PermissionConstants.CAMERA}
                : new String[]{PermissionConstants.CAMERA, PermissionConstants.MICROPHONE};

        XPermission.create(context, permissions).callback(new XPermission.SimpleCallback() {
            @Override
            public void onGranted() {
                Intent intent = new Intent(context, CameraActivity.class);
                startActivityForResult(intent, MatisseConst.REQUEST_CODE_CAPTURE);
            }

            @Override
            public void onDenied() {
                AToast.warning("没有权限，无法使用该功能");
            }
        }).request();
    }
}
