package minor.dokterstas;

/**
 * Created by robert on 7-2-2017.
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import minor.dokterstas.database.DatabaseHelper;

public class ExpandableListAdapter extends BaseExpandableListAdapter{
    private final SparseArray<Group> groups;
    public LayoutInflater inflater;
    public Activity activity;
    private MainActivity context;
    private DatabaseHelper db;


    public ExpandableListAdapter(Activity act, SparseArray<Group> groups, MainActivity context, DatabaseHelper db) {
        this.db = db;
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
        this.context = context;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    private ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String children = (String) getChild(groupPosition, childPosition);
        CheckBox text = null;
        TextView text2 = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }
        text = (CheckBox) convertView.findViewById(R.id.checkbox);
        text2 = (TextView) convertView.findViewById(R.id.TextView);

        final String[] separated = children.split("/");

        text.setText(separated[0]);
        text2.setText(separated[2]);
        ArrayList<View> allViewsWithinMyTopView = getAllChildren(convertView);
        CheckBox test = (CheckBox) allViewsWithinMyTopView.get(1);

        test.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View arg1) {
                CheckBox test = (CheckBox) arg1;
                Log.v("long clicked", "" + test.getTag());

                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.item_edit);
                dialog.setTitle("Confirmation");

                TextView text = (TextView) dialog.findViewById(R.id.textView2);
                Button btnDelete = (Button) dialog.findViewById(R.id.delete);
                Button btnCancel = (Button) dialog.findViewById(R.id.cancel);
                Button btnDate = (Button) dialog.findViewById(R.id.date);

                text.setText(test.getText());

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.deleteItem(separated[1]);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setMessage(separated[0] + " verwijderd uit lijst." );
                        builder.setTitle("Verwijderd");

                        AlertDialog dialog2 = builder.create();
                        dialog2.show();

                        dialog.dismiss();

                        context.createList();

                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                return true;
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View arg1) {

                Log.v("long clicked","clicked: ");

                return true;
            }
        });
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }
        Group group = (Group) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
