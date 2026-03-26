#include<iostream>
#include<string>
#include<fstream>
using namespace std;

void add_donor(){
		string donors[100][5]={};
		char choice='y';
		
		int i=0;
		
		while(choice!='n'){
	  int j=0;
		string first_name, last_name,full_name;
		cout<<endl;
		cout<<"Enter first name: ";
		cin>>first_name;
		cout<<"Enter last name: ";
		cin>>last_name;
		full_name=first_name + last_name;
		donors[i][j]=full_name;
		j++;
		
		string age;
		cout<<"Enter age: ";
		cin>>age;
		donors[i][j]=age;
		j++;
		
		string blood_group;
		cout<<"Enter blood group (a+,a-,b+,b-,ab+,ab-,o+,o-): ";
		cin>>blood_group;
		donors[i][j]=blood_group;
		j++;
		
		string city;
		cout<<"Enter city: ";
		cin>>city;
		donors[i][j]=city;
		j++;
		
		string contact;
		cout<<"Enter contact: ";
		cin>>contact;
		donors[i][j]=contact;
		i++;
		
		cout<<"Do you want to add another donor? (y/n): ";
		cin>>choice;
		}
		cout<<endl<<endl;
		cout<<"The Database has been created...";
		
		ofstream out("test.txt");
		for (int m=0;m<i;m++){
		for(int n=0;n<5;n++){
						out<<donors[m][n]<<" ";
						}
						out<<endl;
						}
		}

void blood_bank(){
		int blood_stock[8];
		ifstream in("blood_stock.txt"); 
		for(int i=0; i<8; i++){ 
			in>>blood_stock[i]; 
			}
		string g;
		cout<<"Enter blood group: (a+,a-,b+,b-,ab+,ab-,o+,o-) ";
		cin>>g;
		if(g=="a+"){
								char c;
								cout<<"You want to add or remove from stock: (a/r) ";
								cin>>c;
								int quantity;
								if(c=='a'){
								cout<<"Enter quantity to add: ";
								cin>>quantity;
								blood_stock[0] +=quantity;
								cout<<"Quantity Added...";
								}
								else if(c=='r'){
								cout<<"Enter quantity to remove: ";
								cin>>quantity;
								if(quantity > blood_stock[0]){
														cout<<"Not enough quantity: ";
														}
								else{
								blood_stock[0] -=quantity;
								cout<<"Quantity removed...";
								}}
								}
		else if(g=="a-"){
								char c;
								cout<<"You want to add or remove from stock: (a/r) ";
								cin>>c;
								int quantity;
								if(c=='a'){
								cout<<"Enter quantity to add: ";
								cin>>quantity;
								blood_stock[1] +=quantity;
								cout<<"Quantity Added...";

								}
								else if(c=='r'){
								cout<<"Enter quantity to remove: ";
								cin>>quantity;
								if(quantity > blood_stock[1]){
														cout<<"Not enough quantity: ";
														}
								else{
								blood_stock[1] -=quantity;
								cout<<"Quantity removed...";
								}}
								}
		else if(g=="b+"){
								char c;
								cout<<"You want to add or remove from stock: (a/r) ";
								cin>>c;
								int quantity;
								if(c=='a'){
								cout<<"Enter quantity to add: ";
								cin>>quantity;
								blood_stock[2] +=quantity;
								cout<<"Quantity Added...";
								}
								else if(c=='r'){
								cout<<"Enter quantity to remove: ";
								cin>>quantity;
								if(quantity > blood_stock[2]){
														cout<<"Not enough quantity: ";
														}
								else{
								blood_stock[2] -=quantity;
								cout<<"Quantity removed...";
								}}
								}
		else if(g=="b-"){
								char c;
								cout<<"You want to add or remove from stock: (a/r) ";
								cin>>c;
								int quantity;
								if(c=='a'){
								cout<<"Enter quantity to add: ";
								cin>>quantity;
								blood_stock[3] +=quantity;
								cout<<"Quantity Added...";
								}
								else if(c=='r'){
								cout<<"Enter quantity to remove: ";
								cin>>quantity;
								if(quantity > blood_stock[3]){
														cout<<"Not enough quantity: ";
														}
								else{
								blood_stock[3] -=quantity;
								cout<<"Quantity removed...";
								}}
								}
		else if(g=="ab+"){
								char c;
								cout<<"You want to add or remove from stock: (a/r) ";
								cin>>c;
								int quantity;
								if(c=='a'){
								cout<<"Enter quantity to add: ";
								cin>>quantity;
								blood_stock[4] +=quantity;
								cout<<"Quantity Added...";
								}
								else if(c=='r'){
								cout<<"Enter quantity to remove: ";
								cin>>quantity;
								if(quantity > blood_stock[4]){
														cout<<"Not enough quantity: ";
														}
								else{
								blood_stock[4] -=quantity;
								cout<<"Quantity removed...";
								}}
								}
		else if(g=="ab-"){
								char c;
								cout<<"You want to add or remove from stock: (a/r) ";
								cin>>c;
								int quantity;
								if(c=='a'){
								cout<<"Enter quantity to add: ";
								cin>>quantity;
								blood_stock[5] +=quantity;
								cout<<"Quantity Added...";
								}
								else if(c=='r'){
								cout<<"Enter quantity to remove: ";
								cin>>quantity;
								if(quantity > blood_stock[5]){
														cout<<"Not enough quantity: ";
														}
								else{
								blood_stock[5] -=quantity;
								cout<<"Quantity removed...";
								}}
								}
		else if(g=="o+"){
								char c;
								cout<<"You want to add or remove from stock: (a/r) ";
								cin>>c;
								int quantity;
								if(c=='a'){
								cout<<"Enter quantity to add: ";
								cin>>quantity;
								blood_stock[6] +=quantity;
								cout<<"Quantity Added...";
								}
								else if(c=='r'){
								cout<<"Enter quantity to remove: ";
								cin>>quantity;
								if(quantity > blood_stock[6]){
														cout<<"Not enough quantity: ";
														}
								else{
								blood_stock[6] -=quantity;
								cout<<"Quantity removed...";
								}}
								}
								
		else if(g=="o-"){
								char c;
								cout<<"You want to add or remove from stock: (a/r) ";
								cin>>c;
								int quantity;
								if(c=='a'){
								cout<<"Enter quantity to add: ";
								cin>>quantity;
								blood_stock[7] +=quantity;
								cout<<"Quantity Added...";
								}
								else if(c=='r'){
								cout<<"Enter quantity to remove: ";
								cin>>quantity;
								if(quantity > blood_stock[7]){
														cout<<"Not enough quantity: ";
														}
								else{
								blood_stock[7] -=quantity;
								cout<<"Quantity removed...";
								}}
								}
		
		ofstream out("blood_stock.txt"); 
			for(int i=0; i<8; i++){ 
							out<<blood_stock[i]<<" "; 
							}
								}
		
						
void show_donors(){
		ifstream in("data.txt");
		string data[100][5];
		for (int m=0;m<100;m++){
			for(int n=0;n<5;n++){
				in>>data[m][n];
				}
		}
						
		for (int m=0;m<100;m++){
			for(int n=0;n<5;n++){
				cout<<data[m][n]<<" ";
				}
			cout<<endl;
			}
		}
		
						
void search_donor(){
		 ifstream in("data.txt");
		string data[100][5];
		for (int m=0;m<100;m++){
		for(int n=0;n<5;n++){
						in>>data[m][n];
						}
						}
		bool found=false;
		string ch;
		cout<<"enter blood group: ";
		cin>>ch;
		cout<<endl;
		for (int m=0;m<100;m++){
						if(data[m][2] == ch){
						cout<<"NAME: "<<data[m][0]<<" ";
						cout<<"AGE: "<<data[m][1]<<" ";
						cout<<"CITY: "<<data[m][3]<<" ";
						cout<<"CONTACT: "<<data[m][4]<<endl;
						found =true;
						}
						}
						if(found==false)
						cout<<"No record found for " <<ch<< " group";;
						}

								
void donate_blood(){
		 int blood_stock[8];
			ifstream in("blood_stock.txt"); 
			for(int i=0; i<8; i++){ 
							in>>blood_stock[i]; 
							}
		int quantity;
		cout<<"Enter quantity to donate: ";
		cin>>quantity;
		string g;
		cout<<"Enter your blood group: (a+,a-,b+,b-,ab+,ab-,o+,o-) ";
		cin>>g;
		if(g=="a+"){
								blood_stock[0] +=quantity;
								}
		else if(g=="a-"){
								blood_stock[1] +=quantity;
								}
								
		else if(g=="b+"){
								blood_stock[2] +=quantity;
								}
		else if(g=="b-"){
								blood_stock[3] +=quantity;
								}
		else if(g=="ab+"){
								blood_stock[4] +=quantity;
								}
		else if(g=="ab-"){
								blood_stock[5] +=quantity;
								}
		else if(g=="o+"){
								blood_stock[6] +=quantity;
								}
		else if(g=="o-"){
								blood_stock[7] +=quantity;
								}
		cout<<endl<<"Thank You for donating blood...";

		
		ofstream out("blood_stock.txt"); 
			for(int i=0; i<8; i++){ 
							out<<blood_stock[i]<<" "; 
							}
		 }
		 
void acquire_blood(){
		int blood_stock[8];
		ifstream in("blood_stock.txt"); 
		for(int i=0; i<8; i++){ 
						in>>blood_stock[i]; 
						}
		int quantity;
		cout<<"Enter quantity you need: ";
		cin>>quantity;
		string g;
		cout<<"Enter blood group you need: (a+,a-,b+,b-,ab+,ab-,o+,o-) ";
		cin>>g;
		if(g=="a+"){
								if(blood_stock[0] < quantity){
								cout<<"Not enough quantity";}
								else{
								blood_stock[0] -=quantity;
								cout<<endl<<"Blood granted...";
								}}
		else if(g=="a-"){
	              if(blood_stock[1] < quantity){
								cout<<"Not enough quantity";}
								else{
								blood_stock[1] -=quantity;
								cout<<endl<<"Blood granted...";
								}}
								
		else if(g=="b+"){
	              if(blood_stock[2] < quantity){
								cout<<"Not enough quantity";}
								else{
								blood_stock[2] -=quantity;
								cout<<endl<<"Blood granted...";
								}}
		else if(g=="b-"){
	              if(blood_stock[3] < quantity){
								cout<<"Not enough quantity";}
								else{
								blood_stock[3] -=quantity;
								cout<<endl<<"Blood granted...";
								}}
		else if(g=="ab+"){
	              if(blood_stock[4] < quantity){
								cout<<"Not enough quantity";}
								else{
								blood_stock[4] -=quantity;
								cout<<endl<<"Blood granted...";
								}}
		else if(g=="ab-"){
	              if(blood_stock[5] < quantity){
								cout<<"Not enough quantity";}
								else{
								blood_stock[5] -=quantity;
								cout<<endl<<"Blood granted...";
								}}
		else if(g=="o+"){
	              if(blood_stock[6] < quantity){
								cout<<"Not enough quantity";}
								else{
								blood_stock[6] -=quantity;
								cout<<endl<<"Blood granted...";
								}}
		else if(g=="o-"){
	              if(blood_stock[7] < quantity){
								cout<<"Not enough quantity";}
								else{
								blood_stock[7] -=quantity;
								cout<<endl<<"Blood granted...";
								}}
		

		
		ofstream out("blood_stock.txt"); 
			for(int i=0; i<8; i++){ 
							out<<blood_stock[i]<<" "; 
							}
		 }
int main(){
		bool loop_again =true;
		cout<<"\n\t\tWelcome to AKS Blood Bank";
		cout<<endl<<"--------------------------------------------------------"<<endl;
		while(loop_again==true){
		int pass,password= 123;
		
		cout<<endl<<"----------"<<endl;
		cout<<"   MENU";
		cout<<endl<<"----------";
		cout<<endl<<endl;
		
		cout<<"Admin Use only"<<endl;
		cout<<"--------------"<<endl;
		cout<<"1. Create database of donors"<<endl;
		cout<<"2. Add or Remove stock of bank"<<endl<<endl;
		cout<<"For User"<<endl;
		cout<<"--------"<<endl;
		cout<<"3. Show all donors"<<endl;
		cout<<"4. Search donor"<<endl;
		cout<<"5. Donate blood"<<endl;
		cout<<"6. Aquire blood"<<endl;
		cout<<"7. Exit"<<endl;
		
		
		int user_choice;
		cout<<"\nEnter service you want: ";
		cin>>user_choice;
		
		switch(user_choice){
				case 1:
					system("cls");
					cout<<"Enter Password!!! ";
					cin>>pass;
														 
					if(pass == password)
					add_donor();
														 
					else
					cout<<"Wrong password";
														 
					break;
				case 2:
					system("cls");
					cout<<"Enter Password!!! ";
					cin>>pass;
														 
					if(pass == password)
					blood_bank();

					else
					cout<<"Wrong password";
														 
					break;
				case 3:
					system("cls");
					show_donors();
					break;
											 			
				case 4:
					system("cls");
					search_donor();
					break;
														 
                case 5:
					system("cls");
					donate_blood();
					break;
				case 6:
					system("cls");
					acquire_blood();
					break;
														 
                case 7:
                	system("cls");
					cout<<"\t..... T H A N K   Y O U ....."<<endl;
					cout<<"AKS softwares   |    All rights reserved   |   2025 "<<endl;
					loop_again=false;
					break;
					}
					}

 cout<<endl;
 system("PAUSE");
 return 0;
}
