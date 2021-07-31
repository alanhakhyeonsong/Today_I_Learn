'use strict';
// Array

// 1. Declaration
const arr1 = new Array();
const arr2 = [1, 2];

// 2. Index position
const fruits = ['🍓', '🍉'];
console.log(fruits);
console.log(fruits.length);
console.log(fruits[0]);
console.log(fruits[fruits.length - 1]);

// 3. Looping over an array
// print all fruits
// a. for
for (let i = 0; i < fruits.length; i++) {
  console.log(fruits[i]);
}

// b. for of
for (let fruit in fruits) {
  console.log(fruit);
}

// c. forEach
fruits.forEach((fruit) => console.log(fruit));

// 4. Addition, deletion, copy
// push: add an item to the end
const hands = new Array();
hands.push('👌', '✌', '👍', '✋');
console.log(hands);

// pop: remove an item from the end
hands.pop();
hands.pop();
console.log(hands);

// unshift: add an item to the beginning
hands.unshift('🙏', '👍');
console.log(hands);

// shift: remove an item from the beginning
hands.shift();
hands.shift();
console.log(hands);

// 📌 shift, unshift are slower than pop, push
// splice: remove an item by index position
hands.push('🤞', '👏');
console.log(hands);
hands.splice(1, 3);
console.log(hands);
hands.splice(1, 1, '🙏', '✍');
console.log(hands);

// combine two arrays
const fruits2 = ['🍒', '🍌'];
const newFruits = fruits.concat(fruits2);
console.log(newFruits);

// 5. Searching
// indexOf: find the index
console.clear();
console.log(hands.indexOf('🙏'));
console.log(hands.indexOf('👀'));

// includes
console.log(hands.includes('👌'));
console.log(hands.includes('🤞'));

// lastIndexOf
console.clear();
hands.push('👌');
console.log(hands);
console.log(hands.indexOf('👌'));
console.log(hands.lastIndexOf('👌'));