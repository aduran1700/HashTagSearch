package com.hashtagsearch;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.hashtagsearch.data.TweetProvider;
import com.hashtagsearch.view.HashtagCursorRecyclerViewAdapter;


public class HashtagListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final int CURSOR_LOADER_ID = 0;
    private HashtagCursorRecyclerViewAdapter hashtagCursorRecyclerViewAdapter;
    private FloatingActionButton fab;
    private OnFragmentInteractionListener mListener;
    private RecyclerView recyclerView;


    public HashtagListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hashtag_list, container, false);
        fab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        hashtagCursorRecyclerViewAdapter = new HashtagCursorRecyclerViewAdapter(getActivity(), mListener, null);
        recyclerView.setAdapter(hashtagCursorRecyclerViewAdapter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View settingDialog = View.inflate(getActivity(), R.layout.dialog_layout, null);
                final EditText input = (EditText) settingDialog.findViewById(R.id.search_text);

                AppCompatDialog dialog = new AlertDialog.Builder(getActivity())
                        .setView(settingDialog)
                        .setTitle(R.string.title_search_text)
                        .setPositiveButton(R.string.search_button_text, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String text = input.getText().toString();

                                if(text.length() > 2) {
                                    if (!text.substring(0, 1).equals("#")) {
                                        text = "#" + text;
                                    }
                                    mListener.onSearchInteraction(text);
                                } else {
                                    Toast.makeText(getActivity(), R.string.hashtag_short_text, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton(R.string.cancel_button_text, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                    }
                                }).
                                setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    public boolean onKey(DialogInterface dialog, int i, KeyEvent keyEvent) {
                                        if (i == KeyEvent.KEYCODE_BACK) {
                                            dialog.dismiss();
                                            return true;
                                        }
                                        return false;
                                    }
                                }).

                                create();

                dialog.show();
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(CURSOR_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), TweetProvider.Hashtags.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        hashtagCursorRecyclerViewAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        hashtagCursorRecyclerViewAdapter.swapCursor(null);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
