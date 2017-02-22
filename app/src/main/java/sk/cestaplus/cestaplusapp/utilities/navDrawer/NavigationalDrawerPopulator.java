package sk.cestaplus.cestaplusapp.utilities.navDrawer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import sk.cestaplus.cestaplusapp.R;
import sk.cestaplus.cestaplusapp.activities.BaterkaActivity;
import sk.cestaplus.cestaplusapp.activities.MainActivity;
import sk.cestaplus.cestaplusapp.activities.OPortaliActivity;
import sk.cestaplus.cestaplusapp.activities.SectionActivity;
import sk.cestaplus.cestaplusapp.activities.SettingsActivity;
import sk.cestaplus.cestaplusapp.activities.konto_activities.LoggedActivity;
import sk.cestaplus.cestaplusapp.activities.konto_activities.NotLoggedActivity;
import sk.cestaplus.cestaplusapp.utilities.SessionManager;
import sk.cestaplus.cestaplusapp.views.AnimatedExpandableListView;

import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_EXTRA_SECTION_ID;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_INTENT_LOAD_BATERKA_ON_TODAY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_MAIN_ACTIVITY;
import static sk.cestaplus.cestaplusapp.extras.IKeys.KEY_PARENT_ACTIVITY;

/**
 * Created by matth on 16.02.2017.
 */
public class NavigationalDrawerPopulator {

    public static final int NO_ICON = -1;
    public static final int DELAY_TO_START_ACTIVITY_MILLIS = 200;

    private AnimatedExpandableListView listView;
    private NavDrawerSectionsAdapter adapter;

    private Context context;
    private final Activity activity;

    public NavigationalDrawerPopulator(Context context, Activity activity) {
        this.activity = activity;
        this.context = context;
    }

    public void populateSectionsExpandableList(){
        SessionManager session = new SessionManager(context);

        final List<GroupItem> groupItems = new ArrayList<>();

        // init class that have to be started for account activity
        Class classToStart;
        if (session.isLoggedIn()) {
            classToStart = LoggedActivity.class;
        } else {
            classToStart = NotLoggedActivity.class;
        }

        groupItems.add(new GroupItem(R.drawable.ic_account_box_black_48dp ,"Konto",
                new ActivityStarter(context, classToStart, KEY_MAIN_ACTIVITY, DELAY_TO_START_ACTIVITY_MILLIS)));

        // init extras for BaterkaActivity
        List<AbstractMap.SimpleEntry<String, Serializable>> toExtras = new ArrayList<>();
        toExtras.add(new AbstractMap.SimpleEntry<String, Serializable>(KEY_INTENT_LOAD_BATERKA_ON_TODAY, true));

        // SOURCE of icon: https://material.io/icons/#ic_highlight
        // good icons too:
        //  https://material.io/icons/#ic_wb_incandescent
        //  https://material.io/icons/#ic_lightbulb_outline
        groupItems.add(new GroupItem(R.drawable.ic_highlight_black_48dp ,"Baterka na dnes",
                new ActivityStarter(context, BaterkaActivity.class, KEY_MAIN_ACTIVITY, DELAY_TO_START_ACTIVITY_MILLIS, toExtras)));


        //region Sections FAMILY

        GroupItem groupFamily = new GroupItem("Rodina", new EmptyAction());

        ChildItemSection chiFamily1 = new ChildItemSection("Milovať a ctiť", "milovatactit");
        ChildItemSection chiFamily2 = new ChildItemSection("A nikdy Ťa neopustím", "anikdytaneopustim");
        ChildItemSection chiFamily3 = new ChildItemSection("Rodičovské skratky", "rodicovskeskratky");
        ChildItemSection chiFamily4 = new ChildItemSection("Dvaja v jednom", "dvajavjednom");

        groupFamily.items.add(chiFamily1);
        groupFamily.items.add(chiFamily2);
        groupFamily.items.add(chiFamily3);
        groupFamily.items.add(chiFamily4);

        groupItems.add(groupFamily);

        // endregion

        //region Sections WORLD

        GroupItem groupWorld = new GroupItem("Svet", new EmptyAction());

        groupWorld.items.add(new ChildItemSection("Kresťan v politike", "krestanvpolitike"));
        groupWorld.items.add(new ChildItemSection("Z parlamentu", "zparlamentu"));
        groupWorld.items.add(new ChildItemSection("Recenzia", "recenzia"));
        groupWorld.items.add(new ChildItemSection("Na pulze", "napulze"));
        groupWorld.items.add(new ChildItemSection("Aktuálne", "aktualne"));

        groupItems.add(groupWorld);

        // endregion

        //region Sections FAITH

        GroupItem groupFaith = new GroupItem("Viera", new EmptyAction());

        groupFaith.items.add(new ChildItemSection("Božia zóna", "boziazona"));
        groupFaith.items.add(new ChildItemSection("Kuchynská teológia", "kuchynskateologia"));
        groupFaith.items.add(new ChildItemSection("Prenasledovaní", "prenasledovani"));

        groupItems.add(groupFaith);

        // endregion

        //region Sections PLUS

        GroupItem groupPlus = new GroupItem("Plus", new EmptyAction());

        groupPlus.items.add(new ChildItemSection("Rozhovor", "rozhovor"));
        groupPlus.items.add(new ChildItemSection("XXL články", "xxlclanky"));
        groupPlus.items.add(new ChildItemSection("Psyché", "psyche"));
        groupPlus.items.add(new ChildItemSection("Poviedky", "poviedky"));

        groupItems.add(groupPlus);

        // endregion

        //region Sections OTHER

        GroupItem groupOther = new GroupItem("Iné", new EmptyAction());

        groupOther.items.add(new ChildItemSection("Slovo+", "slovoplus"));
        groupOther.items.add(new ChildItemSection("Preklady", "preklady"));
        groupOther.items.add(new ChildItemSection("Editoriál", "editorial"));
        groupOther.items.add(new ChildItemSection("Gauč", "gauc"));

        groupItems.add(groupOther);

        // endregion

        groupItems.add(new GroupItem(R.drawable.ic_info_black_48dp ,"O portáli cesta+",
                new ActivityStarter(context, OPortaliActivity.class, KEY_MAIN_ACTIVITY, DELAY_TO_START_ACTIVITY_MILLIS)));

        groupItems.add(new GroupItem(R.drawable.ic_settings_black_48dp ,"Nastavenia",
                new ActivityStarter(context, SettingsActivity.class, KEY_MAIN_ACTIVITY, DELAY_TO_START_ACTIVITY_MILLIS)));

        adapter = new NavDrawerSectionsAdapter(context);
        adapter.setData(groupItems);

        listView = (AnimatedExpandableListView) activity.findViewById(R.id.navDrListViewSections);
        listView.setAdapter(adapter);

        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        /*
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
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
        });
        */

        // In order to show animations, we need to use a custom click handler
        // for our ExpandableListView.
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
            //Toast.makeText(activity.getApplicationContext(), "Group position " + (groupPosition+1), Toast.LENGTH_SHORT).show();

            if (adapter.getChildrenCount(groupPosition) == 0){

                GroupItem groupItem = adapter.getGroup(groupPosition);
                groupItem.action.execute();

                DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.rootDrawerLayout);
                drawerLayout.closeDrawer(Gravity.LEFT); //SOURCE: http://stackoverflow.com/a/32583270

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

                /*Toast.makeText(activity.getApplicationContext(),
                        "Group position " + (groupPosition+1) + " : " + childPosition,
                        Toast.LENGTH_SHORT)
                        .show();
                        */

                ChildItemSection childItemSection = adapter.getChild(groupPosition, childPosition);

                final Intent intent = new Intent(context, SectionActivity.class);
                intent.putExtra(KEY_PARENT_ACTIVITY, KEY_MAIN_ACTIVITY);
                intent.putExtra(KEY_EXTRA_SECTION_ID, childItemSection.sectionId);

                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //SOURCES: http://stackoverflow.com/a/12664620  http://stackoverflow.com/a/12319970

                // delay the start because of onClick animation
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        context.startActivity(intent);
                    }
                }, DELAY_TO_START_ACTIVITY_MILLIS);

                //expandableListView.setItemChecked(childPosition, true);

                return false;
            }
        });
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
        String sectionId;

        public ChildItemSection(String title, String sectionId) {
            this.title = title;
            this.sectionId = sectionId;
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
                convertView = inflater.inflate(R.layout.list_item, parent, false);
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

