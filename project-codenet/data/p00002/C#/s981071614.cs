using System.Collections.Generic;
class p0002
{
    static void Main(string[] args)
    {
        string s;
        List<string> l = new List<string>();
        while ( (s = System.Console.ReadLine()) !=null)
            l.Add(s);
        foreach (var v in l)
        {
            var x = v.IndexOf(" ");
            System.Console.WriteLine(
                (int.Parse(v.Substring(0, x)) + int.Parse(v.Substring(x))).ToString().Length);
        }
       
    }
}