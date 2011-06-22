package edu.cens.text;

import org.rosuda.deducer.Deducer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * Created by IntelliJ IDEA.
 * User: Neal
 * Date: 2/22/11
 * Time: 10:00 PM
 */


public enum ProcessCmd {

    tolower("To Lower Case",        "tm_map(%s, tolower)"),
    deword("Remove Stop Words",     "tm_map(%s, removeWords, stopwords())"){
        @Override
        public JMenuItem[] getExtraOptions() {
            return new JMenuItem[] {
                    new JMenuItem("Print Stopwords"){{
                        addActionListener(new ActionListener() {
                            public void actionPerformed(ActionEvent e) {
                                Deducer.execute("stopwords();");
                            }
                        });
                    }}
            };
        }
    },

    depunct("Remove Punctuation",   "tm_map(%s, removePunctuation)"),
    strip("Remove Whitespace",      "tm_map(%s, stripWhitespace)"),
    stem("Stem Words",              "tm_map(%s, stemDocument)"),
    ;

    public String getRCmd(String... args) {
        return String.format(_function, args);
    }

    public JMenuItem[] getExtraOptions() {return new JMenuItem[0];};

    public final String _label, _function;

    ProcessCmd(String label, String function) {
        _label = label;
        _function = function;
    }

    public String getLabel() {
        return _label;
    }
}
