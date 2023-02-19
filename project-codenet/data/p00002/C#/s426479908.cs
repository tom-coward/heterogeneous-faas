using System;

public class p0002{
	p0002(){
		string str;
		string[] s;
		decimal[] a = new decimal[2];
		
		while((str = Console.ReadLine()) != null){
			s = str.Split(' ');
			for(int i = 0; i < 2; ++i)
				a[i] = decimal.Parse(s[i]);
			Console.WriteLine("{0}", ((a[0]+a[1]).ToString()).Length);
		}
	}
	
	public static int Main(string[] args){
		new p0002();
		return 0;
	}
}