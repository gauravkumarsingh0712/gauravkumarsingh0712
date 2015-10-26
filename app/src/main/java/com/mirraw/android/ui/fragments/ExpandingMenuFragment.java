package com.mirraw.android.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mirraw.android.R;
import com.mirraw.android.models.menus.Menu;
import com.mirraw.android.models.menus.MenuColumn;
import com.mirraw.android.models.menus.MenuItem;
import com.mirraw.android.ui.activities.ProductListingActivity;
import com.mirraw.android.ui.widgets.AnimatedExpandableListView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vihaan on 6/7/15.
 */
public class ExpandingMenuFragment extends Fragment implements View.OnClickListener {
    public static final String sTag = ExpandingMenuFragment.class.getSimpleName();


    private AnimatedExpandableListView mListView;
    private ExampleAdapter mAdapter;
    //    private ArrayList<MenuColumn>  mColumns;
    private List<MenuColumn> mColumns;
    private String mMenuName;
    private static DrawerLayout mDrawerLayout;
    private int mLastExpandedGrp;


    public ExpandingMenuFragment()
    {

    }

    public static ExpandingMenuFragment newInstance(DrawerLayout drawerLayout, String columns){
        ExpandingMenuFragment expandingMenuFragment = new ExpandingMenuFragment();

        mDrawerLayout = drawerLayout;

        Bundle bundle = new Bundle();
        bundle.putString("menu", columns);
        expandingMenuFragment.setArguments(bundle);

        return expandingMenuFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String menuString = bundle.getString("menu");
            Gson gson = new Gson();
            Menu menu = gson.fromJson(menuString, Menu.class);
//            mColumns =  menu.getMenu_columns();
            mColumns = menu.getMenuColumns();
            mMenuName = menu.getTitle();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_expanding_menu, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setMenuName(mMenuName);
        populateExpandingListView();
    }

    private TextView mMenuNameTextView;

    private void initViews(View view) {
        mListView = (AnimatedExpandableListView) view.findViewById(R.id.listView);
        mMenuNameTextView = (TextView) view.findViewById(R.id.menuNameTextView);
        mMenuNameTextView.setOnClickListener(this);
    }


    private void setMenuName(String menuName) {
        mMenuNameTextView.setText(menuName);
    }

    private void populateExpandingListView() {

        final List<GroupItem> items = new ArrayList<GroupItem>();

        // Populate our list with groups and it's children
        if (mColumns != null) {
            MenuColumn column;
//            ArrayList<MenuItem> menuItems;
            List<MenuItem> menuItems;
            MenuItem menuItem;
            for (int i = 0; i < mColumns.size(); i++) {
                GroupItem item = new GroupItem();

                column = mColumns.get(i);
                item.title = column.getTitle();
//                menuItems = column.getMenu_items();
                menuItems = column.getMenuItems();
                for (int j = 0; j < menuItems.size(); j++) {
                    menuItem = menuItems.get(j);
                    ChildItem child = new ChildItem();
                    child.title = menuItem.getTitle();
                    child.key = menuItem.getKey();
                    child.value = menuItem.getValue();
//                    child.hint = "Too awesome";

                    item.items.add(child);
                }

                items.add(item);
            }


            mAdapter = new ExampleAdapter(getActivity());
            mAdapter.setData(items);

            mListView.setAdapter(mAdapter);

            // In order to show animations, we need to use a custom click handler
            // for our ExpandableListView.
            mLastExpandedGrp=-1;
            mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    if (mListView.isGroupExpanded(groupPosition)) {
                        mListView.collapseGroupWithAnimation(groupPosition);
                    } else {

                        if (mLastExpandedGrp != groupPosition) {
                            mListView.collapseGroupWithAnimation(mLastExpandedGrp);
                        }
                        mListView.expandGroupWithAnimation(groupPosition);
                        mLastExpandedGrp = groupPosition;
                    }
                    return true;
                }
            });

            mListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                    GroupItem groupItem = items.get(i);
                    List<ChildItem> childItems = groupItem.items;

                    ChildItem childItem = childItems.get(i1);

                    String title = childItem.title;
                    String key = childItem.key;
                    String value = childItem.value;

                    Intent intent = new Intent(getActivity(), ProductListingActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("title", title);
                    bundle.putString("key", key);
                    bundle.putString("value", value);

                    intent.putExtras(bundle);
                    if (mDrawerLayout != null) {
                        mDrawerLayout.closeDrawers();
                    }

                    startActivity(intent);

                    return false;
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.menuNameTextView:
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }
                break;
        }
    }

    private static class GroupItem {
        String title;
        List<ChildItem> items = new ArrayList<ChildItem>();
    }

    private static class ChildItem {
        String title, key, value;
        String hint;
    }

    private static class ChildHolder {
        TextView title;
        TextView hint;
    }

    private static class GroupHolder {
        TextView title;
    }

    /**
     * Adapter for our list of {@link GroupItem}s.
     */
    private class ExampleAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        private LayoutInflater inflater;

        private List<GroupItem> items;

        public ExampleAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<GroupItem> items) {
            this.items = items;
        }

        @Override
        public ChildItem getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder;
            ChildItem item = getChild(groupPosition, childPosition);
            if (convertView == null) {
                holder = new ChildHolder();
                convertView = inflater.inflate(R.layout.list_item, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
//                holder.hint = (TextView) convertView.findViewById(R.id.textHint);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }

            holder.title.setText(item.title);
//            holder.hint.setText(item.hint);

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return items.get(groupPosition).items.size();
        }

        @Override
        public GroupItem getGroup(int groupPosition) {
            return items.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return items.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            GroupHolder holder;
            GroupItem item = getGroup(groupPosition);
            if (convertView == null) {
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.group_item, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
                convertView.setTag(holder);
            } else {
                holder = (GroupHolder) convertView.getTag();
            }

            if (item.title.length() > 0) {
                item.title = String.valueOf(item.title.charAt(0)).toUpperCase() + item.title.subSequence(1, item.title.length());
            }
            holder.title.setText(item.title);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int arg0, int arg1) {
            return true;
        }

    }

//    http://stackoverflow.com/questions/15207305/getting-the-error-java-lang-illegalstateexception-activity-has-been-destroyed
    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
