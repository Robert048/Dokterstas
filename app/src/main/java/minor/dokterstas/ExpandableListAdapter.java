package minor.dokterstas;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
    private List<List<Item>> children = new ArrayList<>();
    private int counter;


    public ExpandableListAdapter(Activity act, List<Category> groups, MainActivity context, DatabaseHelper db) {
        this.db = db;
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
        this.context = context;

        //Create items in categories
        for (int j = 0; j < groups.size(); j++) {
            List<Item> child = new ArrayList<>();

            for (int i = 0; i < groups.get(j).getItems().size(); i++) {
                child.add(groups.get(j).getItems().get(i));
            }
            children.add(child);
        }

        //set check states
        for (int i = 0; i < children.size(); i++) {
            ArrayList<Integer> tmp = new ArrayList<>();
            for (int j = 0; j < children.get(i).size(); j++) {
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

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        View grid;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        grid = inflater.inflate(R.layout.listrow_details, parent, false);

        final int grpPos = groupPosition;
        final int childPos = childPosition;

        TextView txtData = (TextView) grid.findViewById(R.id.TextView);
        final CheckBox boxNaam = (CheckBox) grid.findViewById(R.id.checkbox);
        boxNaam.setText(getChild(grpPos, childPos).toString());
        if (check_states.get(grpPos).get(childPos) == 1)
            boxNaam.setChecked(true);
        else
            boxNaam.setChecked(false);

        boxNaam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (boxNaam.isChecked()) {
                    counter++;
                    check_states.get(grpPos).set(childPos, 1);
                    boxNaam.setChecked(true);
                } else {
                    counter--;
                    check_states.get(grpPos).set(childPos, 0);
                    boxNaam.setChecked(false);
                }
                if(counter == context.getCounterAmount())
                {
                    Toast.makeText(activity, "Lijst klaar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Item item = (Item) getChild(grpPos, childPos);
        txtData.setText(item.getData());

        boxNaam.setOnLongClickListener(new View.OnLongClickListener()
        {
            @Override
            public boolean onLongClick(View arg1)
            {
                CheckBox checkbox = (CheckBox) arg1;

                final CustomDialog dialog = new CustomDialog(context);
                dialog.setContentView(R.layout.item_edit);
                dialog.setTitle("Confirmation");

                TextView txtTitel = (TextView) dialog.findViewById(R.id.txtTitel);
                final EditText txtVoorraad = (EditText) dialog.findViewById(R.id.txtVoorraad);
                Button btnPlus = (Button) dialog.findViewById(R.id.plus);
                Button btnMinus = (Button) dialog.findViewById(R.id.minus);

                LinearLayout dateLayout = (LinearLayout) dialog.findViewById(R.id.dateLayout);
                LinearLayout stockLayout = (LinearLayout) dialog.findViewById(R.id.stockLayout);
                LinearLayout volumeLayout = (LinearLayout) dialog.findViewById(R.id.volumeLayout);

                final EditText txtVolume = (EditText) dialog.findViewById(R.id.txtVolume);

                TextView txtDate = (TextView) dialog.findViewById(R.id.txtDate);
                Button btnDate = (Button) dialog.findViewById(R.id.date);

                Button btnDelete = (Button) dialog.findViewById(R.id.delete);
                Button btnSave = (Button) dialog.findViewById(R.id.save);
                Button btnCancel = (Button) dialog.findViewById(R.id.cancel);

                switch (item.getType()) {
                    case 0:
                        dateLayout.setVisibility(View.GONE);
                        stockLayout.setVisibility(View.GONE);
                        volumeLayout.setVisibility(View.GONE);
                        btnSave.setVisibility(View.GONE);
                        break;
                    case 1:
                        dateLayout.setVisibility(View.GONE);
                        volumeLayout.setVisibility(View.GONE);
                        break;
                    case 2:
                        stockLayout.setVisibility(View.GONE);
                        volumeLayout.setVisibility(View.GONE);
                        break;
                    case 3:
                        volumeLayout.setVisibility(View.GONE);
                        break;
                    case 4:
                        stockLayout.setVisibility(View.GONE);
                        dateLayout.setVisibility(View.GONE);
                        break;
                    case 5:
                        dateLayout.setVisibility(View.GONE);
                        break;
                    case 6:
                        stockLayout.setVisibility(View.GONE);
                        break;
                }
                txtTitel.setText(checkbox.getText());

                txtVoorraad.setText("" + item.getVoorraad());
                txtDate.setText(item.getTht());

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
                        if (MainActivity.getMinimumStock() >= aantal) {
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
                        ((MainActivity) activity).datePicker("" + item.getID(), dialog);
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
                        final Dialog dialog3 = new Dialog(activity);

                        dialog3.setContentView(R.layout.confirmation);
                        dialog3.setTitle("Confirmation");

                        Button btnYes = (Button) dialog3.findViewById(R.id.yes);
                        Button btnNo = (Button) dialog3.findViewById(R.id.no);

                        dialog3.show();

                        btnYes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                db.deleteItem("" + item.getID());
                                dialog3.dismiss();
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                                builder.setMessage(item.getName() + " verwijderd uit lijst.");
                                builder.setTitle("Verwijderd");

                                context.decreaseCounterAmount();
                                context.createList();

                                AlertDialog dialog4 = builder.create();
                                dialog4.show();

                                dialog.dismiss();
                            }
                        });

                        btnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog3.dismiss();
                            }
                        });




                    }
                });

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO opslaan van volume
                        dialog.dismiss();
                        context.createList();

                        switch (item.getType()) {
                            case 1:
                                //alleen voorraad
                                db.updateStock(item.getID(), txtVoorraad.getText().toString());
                                break;
                            case 2:
                                if (dialog.day == 0)
                                {
                                    //geen datum opgegeven
                                }
                                else {
                                    //alleen datum
                                    db.updateDate(item.getID(), dialog.year, dialog.month, dialog.day);
                                }
                                break;
                            case 3:
                                if (dialog.day == 0) {
                                    //alleen voorraad
                                    db.updateStock(item.getID(), txtVoorraad.getText().toString());
                                } else {
                                    // voorraad en tht
                                    db.updateDate(item.getID(), dialog.year, dialog.month, dialog.day);
                                }
                                break;
                            case 4:
                                db.updateVolume(item.getID(), txtVolume.getText().toString());
                                break;
                            case 5:
                                db.updateVolume(item.getID(), txtVolume.getText().toString());
                                db.updateStock(item.getID(), txtVoorraad.getText().toString());
                                break;
                            case 6:
                                if (dialog.day == 0)
                                {
                                    db.updateVolume(item.getID(), txtVolume.getText().toString());
                                }
                                else
                                {
                                    db.updateVolume(item.getID(), txtVolume.getText().toString());
                                    db.updateDate(item.getID(), dialog.year, dialog.month, dialog.day);
                                }
                                break;
                            case 7:
                                if (dialog.day == 0)
                                {
                                    db.updateVolume(item.getID(), txtVolume.getText().toString());
                                    db.updateStock(item.getID(), txtVoorraad.getText().toString());
                                }
                                else
                                {
                                    db.updateVolume(item.getID(), txtVolume.getText().toString());
                                    db.updateStock(item.getID(), txtVoorraad.getText().toString());
                                    db.updateDate(item.getID(), dialog.year, dialog.month, dialog.day);
                                }
                                break;

                        }
                        ((MainActivity) activity).createList();
                    }
                });
                dialog.show();
                return true;
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

    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }

        CheckedTextView header = (CheckedTextView) convertView.findViewById(R.id.textView1);
        header.setText(getGroup(groupPosition).toString());

        return convertView;
    }

    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public boolean hasStableIds() {
        return true;
    }
}
