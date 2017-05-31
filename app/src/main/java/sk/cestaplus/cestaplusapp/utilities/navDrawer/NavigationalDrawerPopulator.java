package sk.cestaplus.cestaplusapp.utilities.navDrawer;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.BaterkaActivity;
import sk.cestaplus.cestaplusapp.activities.other_activities.OPortaliActivity;
import sk.cestaplus.cestaplusapp.activities.other_activities.SettingsActivity;
import sk.cestaplus.cestaplusapp.extras.Constants;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.utilities.Util;
import sk.cestaplus.cestaplusapp.views.AnimatedExpandableListView;

import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_LOGGED_SUBSCRIPTION_EXPIRED;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_LOGGED_SUBSCRIPTION_OK;
import static sk.cestaplus.cestaplusapp.extras.IErrorCodes.ROLE_NOT_LOGGED;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_INTENT_LOAD_BATERKA_ON_TODAY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;

/**
 * Created by matth on 16.02.2017.
 */
public class NavigationalDrawerPopulator {

    public static final int NO_ICON = -1;

    private AnimatedExpandableListView listView;
    private NavDrawerSectionsAdapter adapter;

    private Context context;
    private final AppCompatActivity activity;

    public NavigationalDrawerPopulator(AppCompatActivity activity) {
        this.activity = activity;
        this.context = activity;
    }

    public AppCompatActivity getActivity() {
        return activity;
    }

    public void populateSectionsExpandableList(){
        SessionManager session = new SessionManager(context);

    // init full name & email text views
        TextView tvFullName = (TextView) activity.findViewById(R.id.tvNavDrFullName);
        TextView tvEmail= (TextView) activity.findViewById(R.id.tvNavDrEmail);

        switch (session.getRole()){
            case ROLE_NOT_LOGGED: {
                tvFullName.setText(context.getString(R.string.not_logged_user));
                tvEmail.setVisibility(View.GONE);
                break;
            }
            case ROLE_LOGGED_SUBSCRIPTION_OK: {
                tvFullName.setText(session.getFullName());
                tvEmail.setText(session.getEmail());
                break;
            }
            case ROLE_LOGGED_SUBSCRIPTION_EXPIRED: {
                tvFullName.setText(session.getFullName());
                tvEmail.setText(session.getEmail());

                // show tvSubscriptionExpired
                TextView tvSubscriptionExpired = (TextView) activity.findViewById(R.id.tvNavDrSubscriptionExpired);
                tvSubscriptionExpired.setVisibility(View.VISIBLE);

            // Adjust bottom margin of tvEmail
                //SOURCES:
                //  http://stackoverflow.com/questions/11121028/load-dimension-value-from-res-values-dimension-xml-from-source-code
                //  http://stackoverflow.com/questions/3277196/can-i-set-androidlayout-below-at-runtime-programmatically

                //Creating a new params
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                // getDimension methods returns dimensions in PIXELS
                int leftPx = (int) context.getResources().getDimension(R.dimen.nav_dr_group_item_title_margin_left);
                int topPx = 0;
                int rightPx = (int) context.getResources().getDimension(R.dimen.nav_dr_group_item_title_margin_right);
                int bottomPx = (int) context.getResources().getDimension(R.dimen.nav_dr_header_full_name_margin_bottom); // CHANGE! - same as full name

                params.setMargins(leftPx, topPx, rightPx, bottomPx); // in PIXELS (left, top, right, bottom);
                params.addRule(RelativeLayout.BELOW, R.id.tvNavDrFullName); //!! don't forget to set all "layout_..." rules from xml, when creating new params ;-)

                tvEmail.setLayoutParams(params);
                //tvEmail.invalidate();
                //tvEmail.requestLayout();

                break;
            }
            default:{
                tvFullName.setText(context.getString(R.string.not_logged_user));
                tvEmail.setVisibility(View.GONE);
            }
        }

    // POPULATE LIST
        final List<GroupItem> groupItems = new ArrayList<>();

        // init class that have to be started for account activity
        Class classToStart = Util.getAccountActivityToStart();

        groupItems.add(new GroupItem(R.drawable.ic_home_black_48dp, context.getString(R.string.home),
                new AllFragmentSwapper(activity)));

        groupItems.add(new GroupItem(R.drawable.ic_account_box_black_48dp ,context.getString(R.string.account_nav_dr),
                new ActivityStarter(context, classToStart, KEY_MAIN_ACTIVITY, Constants.DELAY_TO_START_ACTIVITY_MILLIS)));

        // init extras for BaterkaActivity
        List<AbstractMap.SimpleEntry<String, Serializable>> toExtras = new ArrayList<>();
        toExtras.add(new AbstractMap.SimpleEntry<String, Serializable>(KEY_INTENT_LOAD_BATERKA_ON_TODAY, true));

        // SOURCE of icon: https://material.io/icons/#ic_highlight
        // good icons too:
        //  https://material.io/icons/#ic_wb_incandescent
        //  https://material.io/icons/#ic_lightbulb_outline
        groupItems.add(new GroupItem(R.drawable.ic_highlight_black_48dp ,context.getString(R.string.baterka_on_today),
                new ActivityStarter(context, BaterkaActivity.class, KEY_MAIN_ACTIVITY, Constants.DELAY_TO_START_ACTIVITY_MILLIS, toExtras)));


        //region Sections FAMILY

        GroupItem groupFamily = new GroupItem(context.getString(R.string.family_group_item_title), new EmptyAction());

        ChildItemSection chiFamily1 = new ChildItemSection(activity, context.getString(R.string.milovat_a_ctit_title), context.getString(R.string.milovat_a_ctit_id));
        ChildItemSection chiFamily2 = new ChildItemSection(activity, context.getString(R.string.a_nikdy_ta_neopustim_title), context.getString(R.string.a_nikdy_ta_neopustim_id));
        ChildItemSection chiFamily3 = new ChildItemSection(activity, context.getString(R.string.rodicovske_skratky_title), context.getString(R.string.rodicovske_skratky_id));
        ChildItemSection chiFamily4 = new ChildItemSection(activity, context.getString(R.string.dvaja_v_jednom_title), context.getString(R.string.dvaja_v_jednom_id));

        groupFamily.items.add(chiFamily1);
        groupFamily.items.add(chiFamily2);
        groupFamily.items.add(chiFamily3);
        groupFamily.items.add(chiFamily4);

        groupItems.add(groupFamily);

        // endregion

        //region Sections WORLD

        GroupItem groupWorld = new GroupItem(context.getString(R.string.world_group_item_title), new EmptyAction());

        groupWorld.items.add(new ChildItemSection(activity, context.getString(R.string.krestan_v_politike_title), context.getString(R.string.z_parlamentu_id)));
        groupWorld.items.add(new ChildItemSection(activity, context.getString(R.string.z_parlamentu_title), context.getString(R.string.z_parlamentu_id)));
        groupWorld.items.add(new ChildItemSection(activity, context.getString(R.string.recenzia_title), context.getString(R.string.recenzia_id)));
        groupWorld.items.add(new ChildItemSection(activity, context.getString(R.string.na_pulze_title), context.getString(R.string.na_pulze_id)));
        groupWorld.items.add(new ChildItemSection(activity, context.getString(R.string.aktualne_title), context.getString(R.string.aktualne_id)));

        groupItems.add(groupWorld);

        // endregion

        //region Sections FAITH

        GroupItem groupFaith = new GroupItem(context.getString(R.string.faith_group_item_title), new EmptyAction());

        groupFaith.items.add(new ChildItemSection(activity, context.getString(R.string.bozia_zona_title), context.getString(R.string.bozia_zona_id)));
        groupFaith.items.add(new ChildItemSection(activity, context.getString(R.string.kuchynska_teologia_title), context.getString(R.string.kuchynska_teologia_id)));
        groupFaith.items.add(new ChildItemSection(activity, context.getString(R.string.prenasledovani_title), context.getString(R.string.prenasledovani_id)));

        groupItems.add(groupFaith);

        // endregion

        //region Sections PLUS

        GroupItem groupPlus = new GroupItem(context.getString(R.string.plus_group_item_title), new EmptyAction());

        groupPlus.items.add(new ChildItemSection(activity, context.getString(R.string.rozhovor_title), context.getString(R.string.rozhovor_id)));
        groupPlus.items.add(new ChildItemSection(activity, context.getString(R.string.xxl_clanky_title), context.getString(R.string.xxl_clanky_id)));
        groupPlus.items.add(new ChildItemSection(activity, context.getString(R.string.psyche_title), context.getString(R.string.psyche_id)));
        groupPlus.items.add(new ChildItemSection(activity, context.getString(R.string.poviedky_title), context.getString(R.string.poviedky_id)));

        groupItems.add(groupPlus);

        // endregion

        //region Sections OTHER

        GroupItem groupOther = new GroupItem(context.getString(R.string.other_group_item_title), new EmptyAction());

        groupOther.items.add(new ChildItemSection(activity, context.getString(R.string.slovoplus_title), context.getString(R.string.slovoplus_id)));
        groupOther.items.add(new ChildItemSection(activity, context.getString(R.string.preklady_title), context.getString(R.string.preklady_id)));
        groupOther.items.add(new ChildItemSection(activity, context.getString(R.string.editorial_title), context.getString(R.string.editorial_id)));
        groupOther.items.add(new ChildItemSection(activity, context.getString(R.string.gauc_title), context.getString(R.string.gauc_id)));

        groupItems.add(groupOther);

        // endregion

        groupItems.add(new GroupItem(R.drawable.ic_info_black_48dp, context.getString(R.string.o_portali_nav_dr),
                new ActivityStarter(context, OPortaliActivity.class, KEY_MAIN_ACTIVITY, Constants.DELAY_TO_START_ACTIVITY_MILLIS)));

        groupItems.add(new GroupItem(R.drawable.ic_settings_black_48dp , context.getString(R.string.action_settings),
                new ActivityStarter(context, SettingsActivity.class, KEY_MAIN_ACTIVITY, Constants.DELAY_TO_START_ACTIVITY_MILLIS)));

        adapter = new NavDrawerSectionsAdapter(context);
        adapter.setData(groupItems);

        listView = (AnimatedExpandableListView) activity.findViewById(R.id.navDrListViewSections);
        listView.setAdapter(adapter);
        setListViewHeight(listView, 0); // init expandable list view height !!

        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {

                setListViewHeight(expandableListView, groupPosition); // adjust expandable list view height

                /*  Change background color on click:
                    SOURCE: http://stackoverflow.com/questions/10318642/highlight-for-selected-item-in-expandable-list
                    Take a look at first comment on accepted answer. */
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(groupPosition));
                if (index == 0){
                    expandableListView.setItemChecked(index, true); // check only if 'home' was clicked
                }

                if (adapter.getChildrenCount(groupPosition) == 0){

                    GroupItem groupItem = adapter.getGroup(groupPosition);
                    groupItem.action.execute();

                    closeDrawer();

                    return true;

                } else { // group has some children
                    // We call collapseGroupWithAnimation(int) and
                    // expandGroupWithAnimation(int) to animate group
                    // expansion/collapse.
                    if (listView.isGroupExpanded(groupPosition)) {
                        listView.collapseGroupWithAnimation(groupPosition);
                    } else {
                        listView.expandGroupWithAnimation(groupPosition);
                    }


                    return true;
                }
            }
        });

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view,
                                        int groupPosition, int childPosition, long id) {
                ChildItemSection childItemSection = adapter.getChild(groupPosition, childPosition);
                childItemSection.action.execute();

                /*  Change background color on click:
                    SOURCE: http://stackoverflow.com/questions/10318642/highlight-for-selected-item-in-expandable-list
                    Take a look at first comment on accepted answer. */
                int index = expandableListView.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                expandableListView.setItemChecked(index, true);

                closeDrawer();

                return false;
            }
        });

        listView.setItemChecked(0, true); //set home as checked
    }

    /**
     * Adjust height of expandable list view
     * SOURCE: http://stackoverflow.com/a/36544003
     */
    private void setListViewHeight(ExpandableListView listView, int group) {

        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);

        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();
                }

                totalHeight += (listView.getDividerHeight() * (listAdapter.getChildrenCount(group) - 1));
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void closeDrawer() {
        DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.rootDrawerLayout);
        drawerLayout.closeDrawer(Gravity.LEFT); //SOURCE: http://stackoverflow.com/a/32583270
    }

    //region POJO classes

    public class GroupItem {

        int iconId;
        String title;
        List<ChildItemSection> items = new ArrayList<>();
        IAction action;

        public GroupItem(String title, IAction action) {
            this(NO_ICON, title, action);
        }

        public GroupItem(int iconId, String title, IAction action) {
            this.iconId = iconId;
            this.title = title;
            this.action = action;
        }
    }

    public static class ChildItemSection {
        String title;
        //String sectionId;
        IAction action;

        public ChildItemSection(String title, String sectionId) {
            this.title = title;
        }

        public ChildItemSection(String title, IAction action) {
            this.title = title;
            this.action = action;
        }

        public ChildItemSection(AppCompatActivity activity, String title, String sectionId) {
            this.title = title;
            this.action = new SectionFragmentSwapper(activity, title, sectionId);
        }
    }

    //endregion

    //region HOLDERS

    public static class ChildHolder {
        TextView title;
    }

    public static class GroupHolder {
        ImageView icon;
        TextView title;
    }

    //endregion

    public class NavDrawerSectionsAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
        private LayoutInflater inflater;

        private List<GroupItem> items;

        public NavDrawerSectionsAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void setData(List<GroupItem> items) {
            this.items = items;
        }

        @Override
        public ChildItemSection getChild(int groupPosition, int childPosition) {
            return items.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder holder;
            ChildItemSection item = getChild(groupPosition, childPosition);

            if (convertView == null) {
                holder = new ChildHolder();
                convertView = inflater.inflate(R.layout.child_item, parent, false);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);
                convertView.setTag(holder);
            } else {
                holder = (ChildHolder) convertView.getTag();
            }

            holder.title.setText(item.title);

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
                // create new group holder
                holder = new GroupHolder();
                convertView = inflater.inflate(R.layout.group_item, parent, false);

                //find views
                holder.icon = (ImageView) convertView.findViewById(R.id.ivIcon);
                holder.title = (TextView) convertView.findViewById(R.id.textTitle);

                convertView.setTag(holder);

            } else {
                holder = (GroupHolder) convertView.getTag();
            }

            // if group doesn't have children, show its icon
            // if group has children, show corresponding group indicator (expanded / not expanded)
            //SOURCES: - hide group indicator for groups without child
            //  http://stackoverflow.com/a/11416765
            //  http://mylifewithandroid.blogspot.sk/2011/06/hiding-group-indicator-for-empty-groups.html
            //  https://material.io/icons/#ic_expand_less
            if ( getChildrenCount( groupPosition ) == 0 ) {
                holder.icon.setImageResource(item.iconId);
            } else {
                holder.icon.setImageResource( isExpanded ? R.drawable.ic_expand_less_black_48dp : R.drawable.ic_expand_more_black_48dp );
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
}

