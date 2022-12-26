/* This file was generated by SableCC (http://www.sablecc.org/). */

package soot.jimple.parser.node;

import soot.jimple.parser.analysis.*;

@SuppressWarnings("nls")
public final class TCmplt extends Token
{
    public TCmplt()
    {
        super.setText("<");
    }

    public TCmplt(int line, int pos)
    {
        super.setText("<");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TCmplt(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTCmplt(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TCmplt text.");
    }
}
