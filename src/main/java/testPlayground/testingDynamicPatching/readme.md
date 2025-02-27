there are 20 characters

can only select 1 of each 
	singletons 
		lazy loaded 
		
20 Enums 
winrates for each enumType 
20 classes? <- asnwer this question

factory class 
	<-- helps 'release new heroes' 
	<-- helps keep track of sample size 
	<-- helps keep track of which enums to pull 
	

auto balancing for individual heros
auto balancing for whole classes 

static MageProperties
static FighterProperties 
static TankProperties 
	default hp/atk values 
	holds references to loaded classes 


Player class 
	proficiency with class
	proficiency with hero 
	winrates per class/hero
	
HeroEnum -> maps to ClassEnum, and default values 

balance
	-> just use for all 'Heroes atm
	
AI Champ select 

class

enumSingletons  	

HeroType 
	list of Spells
	Atk Hp