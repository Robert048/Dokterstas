package minor.dokterstas;


import android.app.Activity;
import android.app.Notification;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import java.util.ArrayList;
import java.util.List;

import minor.dokterstas.database.DatabaseHelper;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    public LayoutInflater inflater;
    public Activity activity;
    private MainActivity context;
    private DatabaseHelper db;
    ArrayList<ArrayList<Integer>> check_states = new ArrayList<>();
    private List<Category> groups;
    private List<List<Item>> children;


    public ExpandableListAdapter(Activity act, List<Category> groups, List<List<Item>> children, MainActivity context, DatabaseHelper db) {
        this.db = db;
        activity = act;
        this.groups = groups;
        this.children = children;
        inflater = act.getLayoutInflater();
        this.context = context;

        for(int i = 0; i < children.size(); i++) {
            ArrayList<Integer> tmp = new ArrayList<>();
            for(int j = 0; j < children.get(i).size(); j++) {
                tmp.add(0);
            }
            check_states.add(tmp);
        }
    }

    //Get Methods
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        View grid;


        LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        grid = inflater.inflate(R.layout.listrow_details, parent, false);

        final int grpPos = groupPosition;
        final int childPos = childPosition;

        final CheckBox header = (CheckBox)grid.findViewById(R.id.checkbox);
        header.setText(getChild(groupPosition, childPosition).toString());
        if(check_states.get(grpPos).get(childPos) == 1)
            header.setChecked(true);
        else
            header.setChecked(false);

        header.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(header.isChecked()) {
                    check_states.get(grpPos).set(childPos, 1);
                    header.setChecked(true);
                }
                else
                {
                    check_states.get(grpPos).set(childPos, 0);
                    header.setChecked(false);
                }

            }
        });

        return grid;
    }

    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView,
                             ViewGroup parent) {
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }

        CheckedTextView header = (CheckedTextView)convertView.findViewById(R.id.textView1);
        header.setText(getGroup(groupPosition).toString());

        return convertView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }



/*
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
            ArrayList<View> viewArrayList = new ArrayList<>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<>();

        ViewGroup vg = (ViewGroup) v;
        for (int i = 0; i < vg.getChildCount(); i++) {

            View child = vg.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<>();
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
        CheckBox txtNaam;
        TextView txtData;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }
        txtNaam = (CheckBox) convertView.findViewById(R.id.checkbox);
        txtData = (TextView) convertView.findViewById(R.id.TextView);

        final String[] separated = children.split("-");

        txtNaam.setText(separated[0]);
        txtData.setText(separated[2]);
        ArrayList<View> allViewsWithinMyTopView = getAllChildren(convertView);
        CheckBox checkbox = (CheckBox) allViewsWithinMyTopView.get(1);

        checkbox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg1) {
                CheckBox checkbox = (CheckBox) arg1;
                Cursor dbItem = db.getItem(separated[1]);

                final CustomDialog dialog = new CustomDialog(context);
                dialog.setContentView(R.layout.item_edit);
                dialog.setTitle("Confirmation");

                TextView txtTitel = (TextView) dialog.findViewById(R.id.txtTitel);
                final EditText txtVoorraad = (EditText) dialog.findViewById(R.id.txtVoorraad);
                Button btnPlus = (Button) dialog.findViewById(R.id.plus);
                Button btnMinus = (Button) dialog.findViewById(R.id.minus);

                LinearLayout dateLayout = (LinearLayout) dialog.findViewById(R.id.dateLayout);
                LinearLayout stockLayout = (LinearLayout) dialog.findViewById(R.id.stockLayout);

                TextView txtDate = (TextView) dialog.findViewById(R.id.txtDate);
                Button btnDate = (Button) dialog.findViewById(R.id.date);

                Button btnDelete = (Button) dialog.findViewById(R.id.delete);
                Button btnSave = (Button) dialog.findViewById(R.id.save);
                Button btnCancel = (Button) dialog.findViewById(R.id.cancel);

                int type = 3;
                if(dbItem.moveToFirst()){
                    type = dbItem.getInt(dbItem.getColumnIndex("TYPE"));
                }

                switch (type) {
                    case 0:
                        dateLayout.setVisibility(View.GONE);
                        stockLayout.setVisibility(View.GONE);
                        btnSave.setVisibility(View.GONE);
                        break;
                    case 1:
                        dateLayout.setVisibility(View.GONE);
                        break;
                    case 2:
                        stockLayout.setVisibility(View.GONE);
                        break;
                }
                txtTitel.setText(checkbox.getText());

                String ID = separated[1];
                Cursor c = db.getItem(ID);
                String voorraad = "";
                if(c.moveToFirst()){
                    voorraad = c.getString(c.getColumnIndex("STOCK"));
                }

                long date = 0;


                if(c.moveToFirst()){
                    date = c.getLong(c.getColumnIndex("EXPIRATION"));
                }

                DateTime dTime = new DateTime();
                dTime = dTime.withMillis(date);

                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd/MM/yyyy");

                txtVoorraad.setText(voorraad);
                txtDate.setText(dTime.toString(dateTimeFormatter));

                btnPlus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String stock = txtVoorraad.getText().toString();
                        int aantal = Integer.parseInt(stock);
                        aantal = aantal + 1;
                        txtVoorraad.setText("" + aantal);
                    }
                });

                btnMinus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String stock = txtVoorraad.getText().toString();
                        int aantal = Integer.parseInt(stock);
                        aantal = aantal - 1;
                        txtVoorraad.setText("" + aantal);
                        if(MainActivity.getMinimumStock() >= aantal)
                        {
                            CharSequence text = "Lage voorraad";
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });

                btnDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //TODO Date button
                        ((MainActivity)activity).datePicker(separated[1],dialog);

                    }


                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                btnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //TODO confirmation box
                        db.deleteItem(separated[1]);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        builder.setMessage(separated[0] + " verwijderd uit lijst.");
                        builder.setTitle("Verwijderd");

                        AlertDialog dialog2 = builder.create();
                        dialog2.show();

                        dialog.dismiss();
                        context.createList();
                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        db.setStock(separated[1], txtVoorraad.getText().toString());
                        dialog.dismiss();
                        context.createList();

                        Cursor dbItem = db.getItem(separated[1]);
                        int type = 0;
                        if(dbItem.moveToFirst()){
                            type = dbItem.getInt(dbItem.getColumnIndex("TYPE"));
                        }

                        switch (type) {
                            case 2:
                                db.updateDate(Integer.parseInt(separated[1]),dialog.year,dialog.month,dialog.day);
                                break;
                            case 3:
                                db.updateDate(Integer.parseInt(separated[1]),dialog.year,dialog.month,dialog.day);
                                break;
                        }
                        ((MainActivity)activity).createList();
                    }
                });

                dialog.show();
                return true;
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg1) {

                Log.v("long clicked", "clicked: ");

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

*/
}
