using System;
 
class Program{
	public static void Main(){
		string s;
		while((s = Console.ReadLine()) != null){
			int[] n = Array.ConvertAll(s.Split(' '), int.Parse);
			int sum = n[0] + n[1];
			Console.WriteLine(sum.ToString().Length);
		}
	}
}