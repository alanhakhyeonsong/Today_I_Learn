// JSON
// JavaScript Object Notation

// 1. Object to JSON
// stringify(obj)
let json = JSON.stringify(true);
console.log(json);

json = JSON.stringify(['apple', 'macbook']);
console.log(json);

const mac = {
  name: 'macbook-pro',
  color: 'spacegray',
  size: '13',
  buyDate: new Date(),
  // 함수는 포함 x(데이터만 json에 포함됨)
  run: () => {
    console.log(`${this.name} running`);
  },
};

json = JSON.stringify(mac);
console.log(json);

json = JSON.stringify(mac, ['name']);
console.log(json);

json = JSON.stringify(mac, (key, value) => {
  console.log(`key: ${key}, value: ${value}`);
  return key === 'name' ? 'macbook-pro m1' : value;
});
console.log(json);

// 2. JSON to Object
// parse(json)
console.clear();
json = JSON.stringify(mac);
console.log(json);
const obj = JSON.parse(json, (key, value) => {
  console.log(`key: ${key}, value: ${value}`);
  return value;
});
console.log(obj);
mac.run();
// obj.run();

console.log(mac.buyDate.getDate());
console.log(obj.getDate);