package com.ssract.one.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ccsky.sfish.R;
import com.core.base.request.SRequestAsyncTask;
import com.ssract.one.adapter.ApkInfoAdapter;
import com.ssract.one.bean.ApkInfoBean;
import com.ssract.one.utils.ApkInfoBeanComparator;
import com.ssract.one.utils.AppInfoUtil;
import com.ssract.one.utils.DialogUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ApkInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ApkInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ApkInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String title;
    private String titleIndex;

    private OnFragmentInteractionListener mListener;

    private View mLayoutView;
    private RecyclerView mRecyclerView;

    private List<ApkInfoBean> apkInfoBeans;
    private List<ApkInfoBean> apkInfoBeans_systemApp;
    private List<ApkInfoBean> apkInfoBeans_userApp;

    private ProgressDialog progressDialog;

    private ApkInfoAdapter apkInfoAdapter;
    private static final String TAG = "ApkInfoFragment";

    public ApkInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ApkInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ApkInfoFragment newInstance(String param1, String param2) {
        ApkInfoFragment fragment = new ApkInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_PARAM1);
            titleIndex = getArguments().getString(ARG_PARAM2);
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        mLayoutView = inflater.inflate(R.layout.activity_main_extract, container, false);
        mRecyclerView = mLayoutView.findViewById(R.id.apk_info_rec);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        progressDialog = DialogUtil.createDialog(getActivity(), getResources().getText(R.string.app_loading));
        apkInfoBeans = new ArrayList<>();
        apkInfoBeans_systemApp = new ArrayList<>();
        apkInfoBeans_userApp = new ArrayList<>();

        new SRequestAsyncTask(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                progressDialog.show();
            }

            @Override
            protected String doInBackground(String... strings) {

                getInstalledApk();
                if (apkInfoBeans != null && !apkInfoBeans.isEmpty()){
                    Collections.sort(apkInfoBeans,new ApkInfoBeanComparator());
                }

                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                if (titleIndex.equals("0")){

                    apkInfoAdapter = new ApkInfoAdapter(getActivity(), apkInfoBeans_userApp);

                }else if (titleIndex.equals("1")){

                    apkInfoAdapter = new ApkInfoAdapter(getActivity(), apkInfoBeans_systemApp);
                }else {

                    apkInfoAdapter = new ApkInfoAdapter(getActivity(), apkInfoBeans);
                }


                mRecyclerView.setAdapter(apkInfoAdapter);

                progressDialog.dismiss();

            }
        }.asyncExcute();



        return mLayoutView;
    }




    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void getInstalledApk() {

        PackageManager packageManager = getActivity().getPackageManager();
        List<PackageInfo> allPackages = packageManager.getInstalledPackages(0);
        if (allPackages == null){
            return;
        }
        for (int i = 0; i < allPackages.size(); i++) {
            PackageInfo packageInfo = allPackages.get(i);
            String path = packageInfo.applicationInfo.sourceDir;
            String name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            Log.i(TAG, path);
            Log.i(TAG, name);

            ApkInfoBean apkInfoBean = new ApkInfoBean();
            apkInfoBean.setAppName(name);
            apkInfoBean.setSourceDir(path);
            apkInfoBean.setPackageName(packageInfo.applicationInfo.packageName);
            Drawable iconDrawable = packageManager.getApplicationIcon(packageInfo.applicationInfo);
            apkInfoBean.setIconDrawable(iconDrawable);

            /* icon1和icon2其实是一样的 */
//            Drawable icon1 = pm.getApplicationIcon(appInfo);// 得到图标信息
//            Drawable icon2 = appInfo.loadIcon(pm);

            try {
                if (AppInfoUtil.isSystemApp(packageInfo)) {
                    Log.e(TAG, name + " is not user app");
                    apkInfoBean.setSystemApp(true);
                    apkInfoBeans_systemApp.add(apkInfoBean);
                }else {
                    apkInfoBeans_userApp.add(apkInfoBean);
                }

            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            apkInfoBeans.add(apkInfoBean);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

