package com.nickrcastro.android.almostthere;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;
import static com.nickrcastro.android.almostthere.MainActivity.TEXT_MSG_TO_SEND;

/**
 * Created by nick on 2/3/2018.
 */

public class TextMsgToSendDialog extends DialogFragment{
    public static String text1 = "Hey, I'm almost there.(Sent via AlmostThere app)";
    public static String text2 = "Your taxi is nearing your destination(Sent via AlmostThere app)";
    public static String text3 = "[Add a saved custom Text - coming soon]";
    public static String text4 = "[Add a saved custom text - coming soon]";
    public static String[] choices =  {text1, text2, text3, text4};
    ListView mListView;
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.choose_text_msg_layout, null);

        final EditText editText = view.findViewById(R.id.custom_text_edit_text);
        TextView defaultTextView = view.findViewById(R.id.default_text_to_send);
        final SharedPreferences pref = getActivity().getSharedPreferences(TEXT_MSG_TO_SEND, MODE_PRIVATE);
        defaultTextView.setText(pref.getString("1", "Prefs didn't work"));

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                String newText = editText.getText().toString();
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    SharedPreferences.Editor sharedPrefEditor = pref.edit();
                    sharedPrefEditor.putString("1", newText ).apply();
                    Toast.makeText(getActivity(), "Custom Text entered", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "NewText:" + pref.getString("1", ""), Toast.LENGTH_SHORT).show();
                    dismiss();
                    return true;
                }
                return false;
            }
        });

        mListView = view.findViewById(R.id.text_to_send_list_view);
        populateListView(mListView, editText);
        
        builder.setView(view)
                .setTitle("Choose text message to send upon arrival:")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newText = editText.getText().toString();
                        if(newText != null && !newText.isEmpty()){
                            SharedPreferences.Editor sharedPrefEditor = pref.edit();
                            sharedPrefEditor.putString("1",newText );
                            Toast.makeText(getActivity(), "Custom Text entered", Toast.LENGTH_SHORT).show();
                            Toast.makeText(getActivity(), "NewText:" + newText, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Dismiss", null);
        return builder.create();
    }

    private void populateListView(ListView listview, final EditText mEditText) {
        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                choices
        );
        listview.setAdapter(mAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                String text = adapterView.getItemAtPosition(pos).toString();
                mEditText.setText(text);
            }
        });
    }
}
