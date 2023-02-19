using System;

class main{
public static void Main(String[] g){
string[] s=new string[200];
for(int i=0;i<200;i++){
s[i]=Console.ReadLine();
}

string[] s1=new string[2];
long a, b, d, dk;

foreach(string sa in s){
if(sa==null)continue;
s1=sa.Split(' ');

a=long.Parse(s1[0]);
b=long.Parse(s1[1]);
d=a+b;
dk=d.ToString().Length;
Console.WriteLine(dk);
}

}}