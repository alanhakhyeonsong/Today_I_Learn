'use strict';

// Callback Hell example
class UserStorage {
  loginUser(id, password) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        if (
          (id === 'ramos' && password === '4') ||
          (id === 'neymar' && password === '10')
        ) {
          resolve(id);
        } else {
          reject(new Error('not found'));
        }
      }, 2000);
    });
  }

  getRoles(user) {
    return new Promise((resolve, reject) => {
      setTimeout(() => {
        if (user === 'ramos') {
          resolve({ name: 'ramos', role: 'defender'});
        } else if(user === 'neymar') {
          resolve({ name: 'neymar', role: 'forward'});
        } else {
          reject(new Error('no access'));
        }
      }, 1000);
    });
  }

  async getUserWithRole(user, password) {
    const id = await this.loginUser(user, password);
    const role = await this.getRoles(id);
    return role;
  }
}

const userStorage = new UserStorage();
const id = prompt('enter your id');
const password = prompt('enter your password');
userStorage.loginUser(id, password)
  .then(userStorage.getRoles)
  .then(user => alert(`Hello ${user.name}, you have a ${user.role} role`))
  .catch(console.log);

userStorage
  .getUserWithRole(id, password)
  .catch(console.log)
  .then(console.log);