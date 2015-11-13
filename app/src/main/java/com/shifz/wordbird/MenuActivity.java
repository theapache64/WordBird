package com.shifz.wordbird;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.shifz.wordbird.adapters.interfaces.CallBack;
import com.shifz.wordbird.adapters.MenuAdapter;
import com.shifz.wordbird.models.Menu;
import com.shifz.wordbird.models.Request;
import com.shifz.wordbird.ui.DialogHelper;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements CallBack {


    private static final String DEVELOPER_EMAIL = "shifar.shifz@gmail.com";
    public static List<Menu> menuList;
    private DialogHelper dialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Preparing menu
        if (menuList == null) {
            menuList = new ArrayList<>();

            //Menu not created, so create

            //'Synonym','Opposite','Meaning','Rhyme','Sentence','Plural','Singular','Past','Present','Start','End','Contain'
            menuList.add(new Menu(R.string.title_synonym, R.string.descr_synonym, Codes.CODE_SYNONYM, true));
            menuList.add(new Menu(R.string.title_opposite, R.string.descr_opposite, Codes.CODE_OPPOSITE, true));
            menuList.add(new Menu(R.string.title_rhyme, R.string.descr_rhyme, Codes.CODE_RHYME, true));
            menuList.add(new Menu(R.string.title_meaning, R.string.descr_meaning, Codes.CODE_MEANING, false));
            menuList.add(new Menu(R.string.title_sentence, R.string.sentence_descr, Codes.CODE_SENTENCE, false));
            menuList.add(new Menu(R.string.title_plural, R.string.plural_descr, Codes.CODE_PLURAL, false));
            menuList.add(new Menu(R.string.title_singular, R.string.singular_descr, Codes.CODE_SINGULAR, false));
            menuList.add(new Menu(R.string.title_past, R.string.past_descr, Codes.CODE_PAST, false));
            menuList.add(new Menu(R.string.title_present, R.string.present_descr, Codes.CODE_PRESENT, false));
            menuList.add(new Menu(R.string.title_start, R.string.start_descr, Codes.CODE_START, true));
            menuList.add(new Menu(R.string.title_end, R.string.end_descr, Codes.CODE_END, true));
            menuList.add(new Menu(R.string.title_contain, R.string.contain_descr, Codes.CODE_CONTAIN, true));
        }

        final RecyclerView rvMenu = (RecyclerView) findViewById(R.id.rvMenu);
        rvMenu.setLayoutManager(new LinearLayoutManager(this));
        dialogHelper = new DialogHelper(this);

        final MenuAdapter menuAdapter = new MenuAdapter(menuList, this);

        rvMenu.setAdapter(menuAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.actionHistory:
                startActivity(new Intent(MenuActivity.this, HistoryActivity.class));
                return true;
            case R.id.actionFeedback:
                sendFeedback();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void sendFeedback() {
        final Intent mailIntent = new Intent(Intent.ACTION_SEND);
        mailIntent.setType("message/rfc822");
        mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{DEVELOPER_EMAIL});
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, "WordBird feedback");
        mailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.enter_some_feedback));
        startActivity(Intent.createChooser(mailIntent, getString(R.string.feedback)));
    }

    @Override
    public void onItemClicked(final int position) {

        final Menu clickedMenu = menuList.get(position);

        //Showing dialog
        dialogHelper.showInputDialog(clickedMenu.getTitle(), clickedMenu.getDescr(), new DialogHelper.DialogInputCallback() {
            @Override
            public void onInput(String input) {

                if (input.trim().isEmpty()) {
                    //Empty input
                    Snackbar.make(findViewById(android.R.id.content), "Invalid word!", Snackbar.LENGTH_LONG)
                            .setAction(R.string.try_again, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //Explicitly calling
                                    onItemClicked(position);
                                }
                            })
                            .show();
                } else {
                    //Valid input
                    final Request newRequest = new Request(input, clickedMenu.getCode());

                    //Starting main activity with selected input
                    final Intent mainActivityIntent = new Intent(MenuActivity.this, MainActivity.class);
                    final Bundle args = new Bundle();
                    args.putSerializable(Request.KEY, newRequest);
                    mainActivityIntent.putExtras(args);
                    startActivity(mainActivityIntent);
                }

            }
        });

    }


    public static class Codes {
        public static final String CODE_SYNONYM = "Synonym";
        public static final String CODE_OPPOSITE = "Opposite";
        public static final String CODE_MEANING = "Meaning";
        public static final String CODE_RHYME = "Rhyme";
        public static final String CODE_SENTENCE = "Sentence";
        public static final String CODE_PLURAL = "Plural";
        public static final String CODE_SINGULAR = "Singular";
        public static final String CODE_PAST = "Past";
        public static final String CODE_PRESENT = "Present";
        public static final String CODE_START = "Start";
        public static final String CODE_END = "End";
        public static final String CODE_CONTAIN = "Contain";
    }
}
