using System;
using System.Collections;
using System.Collections.Generic;

public class Example
{
	public static void Main ()
	{
		string line;
		List <string> resultStr = new List<string> ();

		do { 
			line = Console.ReadLine ();
			if (line != null){
				resultStr.Add (line);

				if (resultStr.Count >= 200){
					break;
				}
			}
		} while (line != null);   

		foreach (string r in resultStr) {

			string[] tempStr = r.Split (' ');

			int resultInteger = int.Parse (tempStr [0]) + int.Parse (tempStr [1]);
			Console.WriteLine (resultInteger.ToString().Length);

		}

	}
}