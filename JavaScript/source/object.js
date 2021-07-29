'use strict'
// Objects
// one of the JavaScript's data types.
// a collection of related data and/or functionality.
// Nearly all objects in JavaScript are instances of Object
// object = { key : value };

// 1. Literals and properties
const obj1 = {};  // 'object literal' syntax
const obj2 = new Object();  // 'object constructor' syntax
function print(person) {
  console.log(person.name);
  console.log(person.age);
}

const ramos = { name: 'ramos', age: 4};
print(ramos);

// with JavaScript magic (dynamically typed language)
// can add properties later
ramos.hasJob = true;
console.log(ramos.hasJob);

// can delete properties later
delete ramos.hasJob;
console.log(ramos.hasJob);

// 2. Computed properties
// key should be always string
console.log(ramos.name);
console.log(ramos['name']);
ramos['hasJob'] = true;
console.log(ramos.hasJob);

function printValue(obj, key) {
  console.log(obj[key]);
}
printValue(ramos, 'name');

// 3. Property value shorthand
const person1 = { name: 'zidane', age: 5};
const person2 = { name: 'benzema', age: 9};
const person3 = { name: 'ronaldo', age: 7};
const person4 = new Person('modric', 10);
console.log(person4);

// 4. Constructor Function
function Person(name, age) {
  // this = {};
  this.name = name;
  this.age = age;
  // return this;
}

// 5. in operator: property existence check (key in obj)
console.log('name' in ramos);
console.log('age' in ramos);
console.log('random' in ramos);
console.log(ramos.random);

// 6. for..in vs for..of
// for (key in obj)
console.clear();
for (let key in ramos) {
  console.log(key);
}

// for (value of iterable)
const array = [1, 2, 4, 5];
for(let value of array) {
  console.log(value);
}

// 7. Fun cloning
// Object.assign(dest, [obj1, obj2, obj3...])
const user = { name: 'pogba', age: '6' };
const user2 = user;
user2.name = 'kimmich';
console.log(user);

// old way
const user3 = {};
for (let key in user) {
  user3[key] = user[key];
}
console.clear();
console.log(user3);

const user4 = Object.assign({}, user);
console.log(user4);

// another example
const fruit1 = { color: 'red' };
const fruit2 = { color: 'blue', size: 'big' };
const mixed = Object.assign({}, fruit1, fruit2);
console.log(mixed.color);
console.log(mixed.size);