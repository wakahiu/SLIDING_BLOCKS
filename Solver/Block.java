public class Block
{
	private int myC;
	private int myR;
	private int length;
	private int width;

	public Block()
	{
	}

	public Block( int L , int W , int R , int C )
	{
		this.myC = C;
		this.myR = R;
		this.length = L;
		this.width = W;
	}

	public int getLength(){
		return length;
	}

	public int getWidth(){
		return width;
	}

	public int getR(){
		return myR;
	}

	public int getC(){
		return myC;
	}

	public int hashCode()
	{
		return this.length << 24 | this.myR << 16 | this.myC << 8 | this.width;
	}
	public String toString()
	{
		return "" + this.length + "*" + this.width + "     (" + this.myR + "," + this.myC + ")";
	}
	public boolean equals( Object Obj)
	{
		if( Obj instanceof Block )
		{
			Block temp = (Block)Obj;
			return temp.length == this.length && temp.width == this.width
			&& temp.myC == this.myC && temp.myR == this.myR;
		}else
		{
			return false;
		}
	}

}